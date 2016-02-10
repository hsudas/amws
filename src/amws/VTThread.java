package amws;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequest;
import com.amazonaws.mws.model.GetReportRequestListRequest;
import com.amazonaws.mws.model.GetReportRequestListResponse;
import com.amazonaws.mws.model.GetReportRequestListResult;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ReportRequestInfo;
import com.amazonaws.mws.model.RequestReportRequest;
import com.amazonaws.mws.model.RequestReportResponse;
import com.amazonaws.mws.model.RequestReportResult;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class VTThread extends Thread
{

    private Connection conn = null;
    private final Config cnfg = new Config();
    private final String accessKeyId = cnfg.getAccessKeyId();
    private final String secretAccessKey = cnfg.getSecretAccessKey();
    private final String merchantId = cnfg.getMerchantId();
    private final String appName = cnfg.getAppName();
    private final String appVersion = cnfg.getAppVersion();
    private final MarketplaceWebService service;

    public VTThread()
    {
        MarketplaceWebServiceConfig mws_config = new MarketplaceWebServiceConfig();
        mws_config.setServiceURL("https://mws.amazonservices.com");
        service = new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, appName, appVersion, mws_config);
    }

    @Override
    public void run()
    {
        conn = vtBaglantisiKur();
        if (conn != null)
        {
            sayacKur();
        }
    }

    /**
     * metodlarin surekli calismasi icin sayac kurar
     */
    public void sayacKur()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                System.out.println("thread basladi");

                List<YeniRaporIstek> listeYeniRaporIstek = yeniRaporIstekleriniKontrolEt();
                if (!listeYeniRaporIstek.isEmpty())
                {
                    for (int i = 0; i < listeYeniRaporIstek.size(); i++)
                    {
                        reportRequest(listeYeniRaporIstek.get(i));
                    }
                }

                List<RaporIstek> listeRaporIstek = raporIstekleriniKontrolEt();
                if (!listeRaporIstek.isEmpty())
                {
                    getReportRequestList(listeRaporIstek);
                }

                List<RaporIstek> listeOlusanRapor = olusanRaporlariKontrolEt();
                if (!listeOlusanRapor.isEmpty())
                {
                    for (int i = 0; i < listeOlusanRapor.size(); i++)
                    {
                        getReport(listeOlusanRapor.get(i));
                    }
                }

            }
        }, 0, 60 * 1000);
    }

    /**
     * vt ye baglanir
     *
     * @return vt connection nesnesi
     */
    public Connection vtBaglantisiKur()
    {
        //vt baglanti stringi
        String connectionUrl = "jdbc:sqlserver://" + cnfg.getVT_IP() + ";databaseName=" + cnfg.getVT_ISIM() + ";";
        Connection con = null;

        try
        {
            //vt baglantisi kuruluyor
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl, cnfg.getVT_KULLANICI_ADI(), cnfg.getVT_SIFRE());
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("e1 : " + e.getMessage());
        }
        catch (SQLException e)
        {
            System.out.println("e2 : " + e.getMessage());
        }

        return con;
    }

    /**
     * status u _DONE_ downloaded ı 1 olmayan kayıtların bilgilerini alir
     *
     * @return
     */
    public List<RaporIstek> olusanRaporlariKontrolEt()
    {
        List<RaporIstek> listeRaporIstek = new ArrayList<>();
        try
        {
            PreparedStatement pst = conn.prepareStatement("SELECT ID, GENERATED_REPORT_ID FROM ROYAL.ROYAL.REPORT_REQUEST WHERE STATUS='_DONE_' AND (DOWNLOADED=0 OR DOWNLOADED IS NULL);");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                RaporIstek ri = new RaporIstek(rs.getInt("ID"), rs.getString("GENERATED_REPORT_ID"));
                listeRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            System.out.println("hata : " + e.getMessage());
        }
        return listeRaporIstek;
    }

    /**
     * amws e baglanıi getReport islemi yapar. dosyayı indirir, dosya
     * iceriginden vt sorgusu hazırlayıp vt ye yazar
     *
     * @param ri : rapor istek nesnesi
     */
    public void getReport(RaporIstek ri)
    {
        GetReportRequest request = new GetReportRequest();
        request.setMerchant(merchantId);

        request.setReportId(ri.getReportRequestID());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssS");
        Date date = new Date();

        String dosyaIsmi = "report_" + ri.getReportRequestID() + "_" + dateFormat.format(date) + ".xml";

        try
        {
            FOS report = new FOS(dosyaIsmi);
            request.setReportOutputStream(report);

            service.getReport(request);

            String raporIcerigi = report.getDosyaIcerigi();
            String[] listeSatirlar = raporIcerigi.split("\n");

            Statement statement = conn.createStatement();
            String sorgu;
            for (int i = 0; i < listeSatirlar.length; i++)
            {
                String sorgu1 = "INSERT INTO ROYAL.ROYAL.REPORT_CONTENTS (REPORT_ID, ROW_ID";
                String sorgu2 = "VALUES (" + ri.getReportRequestID() + ", " + String.valueOf(i);

                String[] satirIcerigi = listeSatirlar[i].split("\t");

                for (int j = 0; j < satirIcerigi.length; j++)
                {
                    satirIcerigi[j] = satirIcerigi[j].replace("'", "''");

                    sorgu1 = sorgu1 + ", Column" + String.valueOf(j + 1);
                    sorgu2 = sorgu2 + ", '" + satirIcerigi[j] + "'";
                }

                sorgu1 = sorgu1 + ")";
                sorgu2 = sorgu2 + ");";
                sorgu = sorgu1 + sorgu2;
                statement.addBatch(sorgu);
            }

            String guncelleme = "UPDATE ROYAL.ROYAL.REPORT_REQUEST SET DOWNLOADED=1 WHERE ID=" + ri.getId() + ";";
            statement.addBatch(guncelleme);

            statement.executeBatch();

            /*
            String genelSorgu="";
            String sorgu = "";
            for (int i = 0; i < listeSatirlar.length; i++)
            {

                String sorgu1 = "INSERT INTO ROYAL.ROYAL.REPORT_CONTENTS (REPORT_ID, ROW_ID";
                String sorgu2 = "VALUES (" + ri.getReportRequestID() + ", " + String.valueOf(i);

                String[] satirIcerigi = listeSatirlar[i].split("\t");

                for (int j = 0; j < satirIcerigi.length; j++)
                {
                    satirIcerigi[j] = satirIcerigi[j].replace("'", "''");

                    sorgu1 = sorgu1 + ", Column" + String.valueOf(j + 1);
                    sorgu2 = sorgu2 + ", '" + satirIcerigi[j] + "'";
                }

                sorgu1 = sorgu1 + ")";
                sorgu2 = sorgu2 + ");";

                sorgu = sorgu1 + sorgu2;
                genelSorgu = genelSorgu + sorgu;
            }
             */
 /*
            String genelSorgu;
            String sorgu1 = "INSERT INTO ROYAL.ROYAL.REPORT_CONTENTS (REPORT_ID, ROW_ID";
            String[] satirIcerigi = listeSatirlar[0].split("\t");
            for (int j = 0; j < satirIcerigi.length; j++)
            {
                sorgu1 = sorgu1 + ", Column" + String.valueOf(j + 1);
            }
            sorgu1 = sorgu1 + ") VALUES ";

            String sorgu = "";
            for (int i = 0; i < listeSatirlar.length; i++)
            {

                //String sorgu1 = "INSERT INTO ROYAL.ROYAL.REPORT_CONTENTS (REPORT_ID, ROW_ID";
                //String sorgu2 = "VALUES (" + ri.getReportRequestID() + ", " + String.valueOf(i);
                String sorgu2 = "( " + ri.getReportRequestID() + ", " + String.valueOf(i);

                satirIcerigi = listeSatirlar[i].split("\t");

                for (int j = 0; j < satirIcerigi.length; j++)
                {
                    satirIcerigi[j] = satirIcerigi[j].replace("'", "''");

                    //sorgu1 = sorgu1 + ", Column" + String.valueOf(j + 1);
                    sorgu2 = sorgu2 + ", '" + satirIcerigi[j] + "'";
                }

                //sorgu1 = sorgu1 + ")";
                sorgu2 = sorgu2 + "),";

                sorgu = sorgu + sorgu2;
                //sorgu = sorgu1 + sorgu2;
                //genelSorgu = genelSorgu + sorgu;
            }

            sorgu = sorgu.substring(0, sorgu.lastIndexOf(","));
            sorgu = sorgu + ";";

            genelSorgu = sorgu1 + sorgu;
            //genelSorgu = genelSorgu + ";UPDATE ROYAL.ROYAL.REPORT_REQUEST SET CONTENT=1 WHERE ID="+ri.getId()+";";
             */
        }
        catch (FileNotFoundException e)
        {
            System.out.println("hata :  " + e.getMessage());
        }
        catch (MarketplaceWebServiceException ex)
        {
            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
        catch (SQLException e)
        {
            System.out.println("hata2 : " + e.getMessage());
        }
    }

    /**
     * status u _SUBMITTED_ yada _IN_PROGRESS_ olan kayitlarin bilgilerini alir
     *
     * @return
     */
    public List<RaporIstek> raporIstekleriniKontrolEt()
    {
        List<RaporIstek> listeRaporIstek = new ArrayList<>();

        try
        {
            PreparedStatement pst = conn.prepareStatement("SELECT ID, REPORT_REQUEST_ID FROM ROYAL.ROYAL.REPORT_REQUEST WHERE STATUS='_SUBMITTED_' OR  STATUS='_IN_PROGRESS_';");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                RaporIstek ri = new RaporIstek(rs.getInt("ID"), rs.getString("REPORT_REQUEST_ID"));
                listeRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            System.out.println("hata : " + e.getMessage());
        }
        return listeRaporIstek;
    }

    /**
     * amws e baglanip ReportRequestList islemi yapar
     *
     * @param listeRaporIstek : ReportRequestList isleminde sorulacak raporların
     * verileri
     */
    public void getReportRequestList(List<RaporIstek> listeRaporIstek)
    {

        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant(merchantId);

        List<String> listeReportRequestID = new ArrayList<>();
        for (int i = 0; i < listeRaporIstek.size(); i++)
        {
            listeReportRequestID.add(listeRaporIstek.get(i).getReportRequestID());
        }

        //reportRequestList e gonderilecek idler
        IdList liste = new IdList(listeReportRequestID);
        System.out.println("idlist : " + liste.getId().toString());

        request.setReportRequestIdList(liste);
        try
        {
            GetReportRequestListResponse response = service.getReportRequestList(request);
            if (response.isSetGetReportRequestListResult())
            {
                GetReportRequestListResult getReportRequestListResult = response.getGetReportRequestListResult();
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListResult.getReportRequestInfoList();
                for (int i = 0; i < reportRequestInfoList.size(); i++)
                {
                    ReportRequestInfo reportRequestInfo = reportRequestInfoList.get(i);
                    if (reportRequestInfo.isSetReportProcessingStatus())
                    {
                        System.out.println("id : " + listeRaporIstek.get(i).getId());
                        System.out.println("ReportProcessingStatus : " + reportRequestInfo.getReportProcessingStatus());

                        vtReportRequestListGuncelle(listeRaporIstek.get(i).getId(), reportRequestInfo);
                    }
                }
            }
        }
        catch (MarketplaceWebServiceException ex)
        {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }

    /**
     * ReportRequestList sonucu alindiktan sonra vt de gerekli yerleri gunceller
     *
     * @param raporID : vt id si
     * @param rri : ReportRequestList sonucu
     */
    public void vtReportRequestListGuncelle(int raporID, ReportRequestInfo rri)
    {
        try
        {
            if (rri.getReportProcessingStatus().equals("_DONE_"))
            {
                PreparedStatement pst = conn.prepareStatement("UPDATE ROYAL.ROYAL.REPORT_REQUEST SET STATUS=?, GENERATED_REPORT_ID=? WHERE ID=?;");
                pst.setString(1, rri.getReportProcessingStatus());
                pst.setString(2, rri.getGeneratedReportId());
                pst.setString(3, String.valueOf(raporID));

                pst.executeUpdate();
            }
            else
            {
                PreparedStatement pst = conn.prepareStatement("UPDATE ROYAL.ROYAL.REPORT_REQUEST SET STATUS=? WHERE ID=?;");
                pst.setString(1, rri.getReportProcessingStatus());
                pst.setString(2, String.valueOf(raporID));

                pst.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            System.out.println("hata : " + e.getMessage());
        }
    }

    /**
     * vt ye girilmis yeni rapor istegi var mı diye kontrol eder
     *
     * @return : yeni isteklerin listesi
     */
    public List<YeniRaporIstek> yeniRaporIstekleriniKontrolEt()
    {
        List<YeniRaporIstek> listeYeniRaporIstek = new ArrayList<>();

        try
        {
            PreparedStatement pst = conn.prepareStatement("SELECT ID, START_DATE, END_DATE, REPORT_TYPE FROM ROYAL.ROYAL.REPORT_REQUEST WHERE STATUS IS NULL;");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                YeniRaporIstek ri = new YeniRaporIstek(rs.getInt("ID"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("REPORT_TYPE"));
                listeYeniRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            System.out.println("hata : " + e.getMessage());
        }

        return listeYeniRaporIstek;
    }

    /**
     * amws e baglanıp requestReport islemi yapar
     *
     * @param ri : yapılacak istegin parametrelerinin oldugu nesne
     */
    public void reportRequest(YeniRaporIstek ri)
    {
        int raporID = ri.getId();
        String baslangic = ri.getBaslangicTarihi();
        String bitis = ri.getBitisTarihi();
        String raporTip = ri.getTip();

        //System.out.println("baslangic : " + baslangic + " -- bitis : " + bitis);
        //MarketplaceWebServiceConfig config = new MarketplaceWebServiceConfig();
        //config.setServiceURL("https://mws.amazonservices.com");
        //MarketplaceWebService service = new MarketplaceWebServiceClient(accessKeyId, secretAccessKey, appName, appVersion, config);
        //final String sellerDevAuthToken = "<Merchant Developer MWS Auth Token>";
        // marketplaces from which data should be included in the report; look at the
        // API reference document on the MWS website to see which marketplaces are
        // included if you do not specify the list yourself
        /*
        final IdList marketplaces = new IdList(Arrays.asList(
                "Marketplae1",
                "Marketplace2"));
         */
        final IdList marketplaces = new IdList(Arrays.asList());

        RequestReportRequest request = new RequestReportRequest()
                .withMerchant(merchantId)
                //.withMarketplaceIdList(marketplaces)
                //.withReportType("_GET_FLAT_FILE_ORDERS_DATA_")
                .withReportType(raporTip)
                .withReportOptions("ShowSalesChannel=true");
        //request = request.withMWSAuthToken(sellerDevAuthToken);

        // demonstrates how to set the date range
        DatatypeFactory df = null;
        try
        {
            df = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try
        {
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            GregorianCalendar cal = new GregorianCalendar();

            Date date = dateformat.parse(baslangic);
            cal.setTime(date);
            XMLGregorianCalendar startDate = df.newXMLGregorianCalendar(cal);

            date = dateformat.parse(bitis);
            cal.setTime(date);
            XMLGregorianCalendar finishDate = df.newXMLGregorianCalendar(cal);

            request.setStartDate(startDate);
            request.setEndDate(finishDate);
        }
        catch (Exception e)
        {
        }

        // @TODO: set additional request parameters here
        //RequestReportResponse response = invokeRequestReport(service, request);
        try
        {
            RequestReportResponse response = service.requestReport(request);
            if (response.isSetRequestReportResult())
            {
                RequestReportResult requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo())
                {
                    ReportRequestInfo reportRequestInfo = requestReportResult.getReportRequestInfo();

                    vtRequestReportGuncelle(raporID, reportRequestInfo);
                }
            }
        }
        catch (MarketplaceWebServiceException ex)
        {

            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
    }

    /**
     * requestReport tan sonra gelen bilgilerle vt yi gunceller
     *
     * @param raporID : requestReport tun vt deki id si
     * @param info : amws den gelen requestReport cevabı
     */
    //public void vtRequestReportGuncelle(int raporID, RequestReportResponse response)
    public void vtRequestReportGuncelle(int raporID, ReportRequestInfo info)
    {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String submitDate = dateformat.format(info.getSubmittedDate().toGregorianCalendar().getTime());

        try
        {
            PreparedStatement pst = conn.prepareStatement("UPDATE ROYAL.ROYAL.REPORT_REQUEST SET STATUS=?, SUBMIT_DATE=?, REPORT_REQUEST_ID=? WHERE ID=?;");
            pst.setString(1, info.getReportProcessingStatus());
            pst.setString(2, submitDate);
            pst.setString(3, info.getReportRequestId());
            pst.setString(4, String.valueOf(raporID));

            pst.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("hata : " + e.getMessage());
        }
    }

}
