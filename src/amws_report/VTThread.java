package amws_report;

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
import java.text.ParseException;
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

import static amws_report.Main.*;

public class VTThread extends Thread
{

    private Config cnfg = null;
    private Connection conn = null;
    private final Private prvt = new Private();
    private final String accessKeyId = prvt.getAccessKeyId();
    private final String secretAccessKey = prvt.getSecretAccessKey();
    private final String merchantId = prvt.getMerchantId();
    private final String appName = prvt.getAppName();
    private final String appVersion = prvt.getAppVersion();
    private final MarketplaceWebService service;

    public VTThread()
    {
        dosyayaYaz("thread olusturuldu");
        cnfg = getCnfg();

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
                dosyayaYaz("------------------islem basladi------------------");


                reportRequestTableViewGuncelle();

                dosyayaYaz("-----report_ basladi------");
                List<YeniRaporIstek> listeYeniRaporIstek = yeniRaporIstekleriniKontrolEt();
                if (!listeYeniRaporIstek.isEmpty())
                {
                    for (int i = 0; i < listeYeniRaporIstek.size(); i++)
                    {
                        reportRequest(listeYeniRaporIstek.get(i));
                    }
                }
                dosyayaYaz("-----reportRequest bitti------");

                dosyayaYaz("-----getReportRequestList basladi------");
                List<Rapor> listeRaporIstek = raporIstekleriniKontrolEt();
                if (!listeRaporIstek.isEmpty())
                {
                    getReportRequestList(listeRaporIstek);
                }
                dosyayaYaz("-----getReportRequestList bitti------");

                dosyayaYaz("-----getReport basladi------");
                List<Rapor> listeOlusanRapor = olusanRaporlariKontrolEt();
                if (!listeOlusanRapor.isEmpty())
                {
                    for (int i = 0; i < listeOlusanRapor.size(); i++)
                    {
                        getReport(listeOlusanRapor.get(i));
                    }
                }
                dosyayaYaz("-----getReport bitti------");
                dosyayaYaz("------------------islem bitti------------------");
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
        dosyayaYaz("veritabani baglantisi kuruluyor");

        //vt baglanti stringi
        String connectionUrl = "jdbc:sqlserver://" + cnfg.getVT_IP() + ";databaseName=" + cnfg.getVT_DATABASE_NAME() + ";";
        Connection con = null;

        try
        {
            //vt baglantisi kuruluyor
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection(connectionUrl, cnfg.getVT_USERNAME(), cnfg.getVT_PASSWORD());

            dosyayaYaz("veritabani baglantisi kuruldu : conn : " + con);
        }
        catch (ClassNotFoundException e)
        {
            dosyayaYaz("hata 12 : " + e.getMessage());
            dosyayaYaz("veritabani baglantisi kurulurken hata olustu");
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 13 : " + e.getMessage());
            dosyayaYaz("veritabani baglantisi kurulurken hata olustu");
        }
        dosyayaYaz("veritabani baglantisi kuruldu");

        return con;
    }

    /**
     * reportRequest tablo icerigini tableView a yazar
     */
    public void reportRequestTableViewGuncelle()
    {
        dosyayaYaz("TableView guncelleniyor");
        try
        {
            listTableViewTemizle();
            PreparedStatement pst = conn.prepareStatement("SELECT START_DATE, END_DATE, REPORT_TYPE, SUBMIT_DATE, STATUS, REPORT_REQUEST_ID, GENERATED_REPORT_ID, DOWNLOADED, DOWNLOAD_TYPE FROM "+ cnfg.getTABLE_REQUEST());
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                tableViewSatirEkle(rs.getString("START_DATE"),
                                   rs.getString("END_DATE"),
                                   rs.getString("REPORT_TYPE"),
                                   rs.getString("SUBMIT_DATE"),
                                   rs.getString("STATUS"),
                                   rs.getString("REPORT_REQUEST_ID"),
                                   rs.getString("GENERATED_REPORT_ID"),
                                   rs.getString("DOWNLOADED"),
                                   rs.getString("DOWNLOAD_TYPE"));
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 16 : " + e.getMessage());
            dosyayaYaz("TableView guncellenirken hata olustu");
        }
        dosyayaYaz("TableView guncellendi");
    }

    /**
     * status u _DONE_ downloaded ı 1 olmayan kayıtların bilgilerini alir
     *
     * @return
     */
    public List<Rapor> olusanRaporlariKontrolEt()
    {
        dosyayaYaz("yeni olusan raporlar veritabanında kontrol ediliyor");

        List<Rapor> listeRaporIstek = new ArrayList<>();
        try
        {
            //PreparedStatement pst = conn.prepareStatement("SELECT ID, GENERATED_REPORT_ID FROM ROYAL.ROYAL.REPORT_REQUEST WHERE STATUS='_DONE_' AND (DOWNLOADED=0 OR DOWNLOADED IS NULL);");
            PreparedStatement pst = conn.prepareStatement("SELECT ID, GENERATED_REPORT_ID FROM "+ cnfg.getTABLE_REQUEST() +" WHERE STATUS='_DONE_' AND (DOWNLOADED=0 OR DOWNLOADED IS NULL);");

            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                dosyayaYaz("yeni olusan rapor var : id : " + rs.getString("GENERATED_REPORT_ID"));

                Rapor ri = new Rapor(rs.getInt("ID"), rs.getString("GENERATED_REPORT_ID"));
                listeRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 1 : " + e.getMessage());
            dosyayaYaz("yeni olusan raporlar kontrol edilirken hata olustu");
        }
        dosyayaYaz("yeni olusan raporlar veritabanında kontrol edildi");

        return listeRaporIstek;
    }

    /**
     * amws e baglanıi getReport islemi yapar. dosyayı indirir, dosya
     * iceriginden vt sorgusu hazırlayıp vt ye yazar
     *
     * @param ri : rapor istek nesnesi
     */
    public void getReport(Rapor ri)
    {
        dosyayaYaz("rapor bilgisayara kaydediliyor - get_report : generated_report_id : " + ri.getReportRequestID());

        GetReportRequest request = new GetReportRequest();
        request.setMerchant(merchantId);

        request.setReportId(ri.getReportRequestID());

        //DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmssS");
        DateFormat dateFormat = new SimpleDateFormat(cnfg.getFILE_NAME_FORMAT());
        Date date = new Date();

        String dosyaIsmi = "report_" + ri.getReportRequestID() + dateFormat.format(date) + ".xml";

        try
        {
            FOS report = new FOS(dosyaIsmi);
            request.setReportOutputStream(report);

            service.getReport(request);

            String raporIcerigi = report.getDosyaIcerigi();
            String[] listeSatirlar = raporIcerigi.split("\n");

            dosyayaYaz("rapor veritabanına kaydediliyor");

            Statement statement = conn.createStatement();
            String sorgu;
            for (int i = 0; i < listeSatirlar.length; i++)
            {
                //String sorgu1 = "INSERT INTO ROYAL.ROYAL.REPORT_CONTENTS (REPORT_ID, ROW_ID";
                String sorgu1 = "INSERT INTO "+ cnfg.getTABLE_CONTENTS() +" (REPORT_ID, ROW_ID";
                String sorgu2 = "VALUES (" + ri.getReportRequestID() + ", " + String.valueOf(i);

                String[] satirIcerigi = listeSatirlar[i].split("\t");

                for (int j = 0; j < satirIcerigi.length; j++)
                {
                    satirIcerigi[j] = satirIcerigi[j].replace("'", "''");
                    satirIcerigi[j] = satirIcerigi[j].replaceAll("[\n\r]", "");

                    sorgu1 = sorgu1 + ", Column" + String.valueOf(j + 1);
                    sorgu2 = sorgu2 + ", '" + satirIcerigi[j] + "'";
                }

                sorgu1 = sorgu1 + ")";
                sorgu2 = sorgu2 + ");";
                sorgu = sorgu1 + sorgu2;

                /*
                PrintWriter out = new PrintWriter(new FileWriter("sorgu.txt", true), true);
                out.write(sorgu + "\n");
                out.close();
                 */
                statement.addBatch(sorgu);
            }

            String guncelleme = "UPDATE "+ cnfg.getTABLE_REQUEST() +" SET DOWNLOADED=1 WHERE ID=" + ri.getId() + ";";
            statement.addBatch(guncelleme);

            statement.executeBatch();

            dosyayaYaz("rapor kaydedildi");


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
            dosyayaYaz("hata 2 : " + e.getMessage());
            dosyayaYaz("rapor kaydedilirken hata olustu");
        }
        catch (MarketplaceWebServiceException ex)
        {
            dosyayaYaz("hata 3 : " + ex.getMessage());
            dosyayaYaz("rapor kaydedilirken hata olustu");

//            System.out.println("Caught Exception: " + ex.getMessage());
//            System.out.println("Response Status Code: " + ex.getStatusCode());
//            System.out.println("Error Code: " + ex.getErrorCode());
//            System.out.println("Error Type: " + ex.getErrorType());
//            System.out.println("Request ID: " + ex.getRequestId());
//            System.out.print("XML: " + ex.getXML());
//            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 4 : " + e.getMessage());
            dosyayaYaz("rapor kaydedilirken hata olustu");
        }

        dosyayaYaz("rapor bilgisayara kaydedildi - get_report : generated_report_id : " + ri.getReportRequestID());
    }

    /**
     * status u _SUBMITTED_ yada _IN_PROGRESS_ olan kayitlarin bilgilerini alir
     *
     * @return
     */
    public List<Rapor> raporIstekleriniKontrolEt()
    {
        dosyayaYaz("yapılan rapor istekleri veritabanında kontrol ediliyor");

        List<Rapor> listeRaporIstek = new ArrayList<>();

        try
        {
            PreparedStatement pst = conn.prepareStatement("SELECT ID, REPORT_REQUEST_ID FROM "+ cnfg.getTABLE_REQUEST() +" WHERE STATUS='_SUBMITTED_' OR  STATUS='_IN_PROGRESS_';");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                dosyayaYaz("hazırlanan rapor bulundu : report_request_id : " + rs.getString("REPORT_REQUEST_ID"));

                Rapor ri = new Rapor(rs.getInt("ID"), rs.getString("REPORT_REQUEST_ID"));
                listeRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 5 : " + e.getMessage());
            dosyayaYaz("yapılan rapor istekleri kontrol edilirken hata olustu");
        }
        dosyayaYaz("yapılan rapor istekleri veritabanında kontrol edildi");

        return listeRaporIstek;
    }

    /**
     * amws e baglanip ReportRequestList islemi yapar
     *
     * @param listeRaporIstek : ReportRequestList isleminde sorulacak raporların
     * verileri
     */
    public void getReportRequestList(List<Rapor> listeRaporIstek)
    {
        dosyayaYaz("rapor isteklerinin durumları kontrol ediliyor - get_report_request_list");

        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant(merchantId);

        List<String> listeReportRequestID = new ArrayList<>();
        for (int i = 0; i < listeRaporIstek.size(); i++)
        {
            listeReportRequestID.add(listeRaporIstek.get(i).getReportRequestID());
        }

        //reportRequestList e gonderilecek idler
        IdList liste = new IdList(listeReportRequestID);
        //System.out.println("idlist : " + liste.getId().toString());

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
                        dosyayaYaz("rapor isteginin durumu : vt_id : " + listeRaporIstek.get(i).getId() + " - report_request_id : " + listeRaporIstek.get(i).getReportRequestID() + " - status : " + reportRequestInfo.getReportProcessingStatus() + " - generated_status_id : " + reportRequestInfo.getGeneratedReportId());

                        vtReportRequestListGuncelle(listeRaporIstek.get(i).getId(), reportRequestInfo);
                    }
                }
            }
        }
        catch (MarketplaceWebServiceException ex)
        {
            dosyayaYaz("hata 6 : " + ex.getMessage());
            dosyayaYaz("rapor isteklerinin durumları kontrol edilirken hata olustu");

//            System.out.println("Caught Exception: " + ex.getMessage());
//            System.out.println("Response Status Code: " + ex.getStatusCode());
//            System.out.println("Error Code: " + ex.getErrorCode());
//            System.out.println("Error Type: " + ex.getErrorType());
//            System.out.println("Request ID: " + ex.getRequestId());
//            System.out.print("XML: " + ex.getXML());
//            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }

        dosyayaYaz("rapor isteklerinin durumları kontrol edildi - get_report_request_list");
    }

    /**
     * ReportRequestList sonucu alindiktan sonra vt de gerekli yerleri gunceller
     *
     * @param raporID : vt id si
     * @param rri : ReportRequestList sonucu
     */
    public void vtReportRequestListGuncelle(int raporID, ReportRequestInfo rri)
    {
        dosyayaYaz("rapor isteginin durumu veritabaninda guncelleniyor 2 : " + rri.getReportProcessingStatus());

        try
        {
            if (rri.getReportProcessingStatus().equals("_DONE_"))
            {
                PreparedStatement pst = conn.prepareStatement("UPDATE "+ cnfg.getTABLE_REQUEST() +" SET STATUS=?, GENERATED_REPORT_ID=? WHERE ID=?;");
                pst.setString(1, rri.getReportProcessingStatus());
                pst.setString(2, rri.getGeneratedReportId());
                pst.setString(3, String.valueOf(raporID));

                pst.executeUpdate();
            }
            else
            {
                PreparedStatement pst = conn.prepareStatement("UPDATE "+ cnfg.getTABLE_REQUEST() +" SET STATUS=? WHERE ID=?;");
                pst.setString(1, rri.getReportProcessingStatus());
                pst.setString(2, String.valueOf(raporID));

                pst.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 7 : " + e.getMessage());
            dosyayaYaz("veritabani guncellenirken hata olustu");
        }

        dosyayaYaz("rapor isteginin durumu veritabaninda guncellendi");
    }

    /**
     * vt ye girilmis yeni rapor istegi var mı diye kontrol eder
     *
     * @return : yeni isteklerin listesi
     */
    public List<YeniRaporIstek> yeniRaporIstekleriniKontrolEt()
    {
        dosyayaYaz("yeni rapor istekleri veritabanında kontrol ediliyor");

        List<YeniRaporIstek> listeYeniRaporIstek = new ArrayList<>();

        try
        {
            PreparedStatement pst = conn.prepareStatement("SELECT ID, START_DATE, END_DATE, REPORT_TYPE FROM "+ cnfg.getTABLE_REQUEST() +" WHERE STATUS IS NULL;");
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                dosyayaYaz("yeni rapor istegi bulundu : vt_id : " + rs.getInt("ID") + " - start : " + rs.getString("START_DATE") + " - end : " + rs.getString("END_DATE") + " - report_type: " + rs.getString("REPORT_TYPE"));

                YeniRaporIstek ri = new YeniRaporIstek(rs.getInt("ID"), rs.getString("START_DATE"), rs.getString("END_DATE"), rs.getString("REPORT_TYPE"));
                listeYeniRaporIstek.add(ri);
            }
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 8 : " + e.getMessage());
            dosyayaYaz("yeni rapor istekleri kontrol edilirken hata olustu");
        }
        dosyayaYaz("yeni rapor istekleri veritabanında kontrol edildi");

        return listeYeniRaporIstek;
    }

    /**
     * amws e baglanıp requestReport islemi yapar
     *
     * @param ri : yapılacak istegin parametrelerinin oldugu nesne
     */
    public void reportRequest(YeniRaporIstek ri)
    {
        dosyayaYaz("sunucudan rapor istegi yapılıyor - request_report : vt_id : " + ri.getId());

        int raporID = ri.getId();
        String baslangic = ri.getBaslangicTarihi();
        String bitis = ri.getBitisTarihi();
        String raporTip = ri.getTip();

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
            // @TODO: set additional request parameters here
            //RequestReportResponse response = invokeRequestReport(service, request);

            RequestReportResponse response = service.requestReport(request);
            if (response.isSetRequestReportResult())
            {
                RequestReportResult requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo())
                {
                    ReportRequestInfo reportRequestInfo = requestReportResult.getReportRequestInfo();

                    dosyayaYaz("rapor isteginin durumu : vt_id : " + raporID + " - report-request_id : " + reportRequestInfo.getReportRequestId() + " - status : " + reportRequestInfo.getReportProcessingStatus());

                    vtRequestReportGuncelle(raporID, reportRequestInfo);
                }
            }
        }
        catch (MarketplaceWebServiceException ex)
        {
            dosyayaYaz("hata 9 : " + ex.getMessage());
            dosyayaYaz("sunucudan rapor istegi yapılırken hata olustu : vt_id: " + ri.getId());

//            System.out.println("Caught Exception: " + ex.getMessage());
//            System.out.println("Response Status Code: " + ex.getStatusCode());
//            System.out.println("Error Code: " + ex.getErrorCode());
//            System.out.println("Error Type: " + ex.getErrorType());
//            System.out.println("Request ID: " + ex.getRequestId());
//            System.out.print("XML: " + ex.getXML());
//            System.out.println("ResponseHeaderMetadata: " + ex.getResponseHeaderMetadata());
        }
        catch (ParseException e)
        {
            dosyayaYaz("hata 10 : " + e.getMessage());
            dosyayaYaz("sunucudan rapor istegi yapılırken hata olustu : vt_id: " + ri.getId());
        }

        dosyayaYaz("sunucudan rapor istegi yapıldı : vt_id: " + ri.getId());
    }

    /**
     * requestReport tan sonra gelen bilgilerle vt yi gunceller
     *
     * @param raporID : requestReport tun vt deki id si
     * @param info : amws den gelen requestReport cevabı
     */
    public void vtRequestReportGuncelle(int raporID, ReportRequestInfo info)
    {
        dosyayaYaz("rapor isteginin durumu veritabaninda guncelleniyor 1 : " + info.getReportProcessingStatus());

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String submitDate = dateformat.format(info.getSubmittedDate().toGregorianCalendar().getTime());

        try
        {
            PreparedStatement pst = conn.prepareStatement("UPDATE "+ cnfg.getTABLE_REQUEST() +" SET STATUS=?, SUBMIT_DATE=?, REPORT_REQUEST_ID=? WHERE ID=?;");
            pst.setString(1, info.getReportProcessingStatus());
            pst.setString(2, submitDate);
            pst.setString(3, info.getReportRequestId());
            pst.setString(4, String.valueOf(raporID));

            pst.executeUpdate();
        }
        catch (SQLException e)
        {
            dosyayaYaz("hata 11 : " + e.getMessage());
            dosyayaYaz("rapor isteginin durumu veritabaninda guncellenirken ");
        }

        dosyayaYaz("rapor isteginin durumu veritabaninda guncellendi");
    }
}
