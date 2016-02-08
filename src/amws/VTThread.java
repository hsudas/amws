package amws;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequestListRequest;
import com.amazonaws.mws.model.GetReportRequestListResponse;
import com.amazonaws.mws.model.GetReportRequestListResult;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ReportRequestInfo;
import com.amazonaws.mws.model.RequestReportRequest;
import com.amazonaws.mws.model.RequestReportResponse;
import com.amazonaws.mws.model.RequestReportResult;
import com.amazonaws.mws.model.ResponseMetadata;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        System.out.println("VTThread : ");
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

        //XMLGregorianCalendar startDate = df.newXMLGregorianCalendar(new GregorianCalendar(2016, 1, 1));
        //request.setStartDate(startDate);
        //request.setEndDate(df.newXMLGregorianCalendar(new GregorianCalendar(2016, 1, 2)));
        // @TODO: set additional request parameters here
        RequestReportResponse response = invokeRequestReport(service, request);

        if (response != null)
        {
            vtRequestReportGuncelle(raporID, response);
        }
        else
        {
            System.out.println("hata");
        }
    }

    /**
     * requestReport tan sonra gelen bilgilerle vt yi gunceller
     *
     * @param raporID : requestReport tun vt deki id si
     * @param response : amws den gelen requestReport cevabı
     */
    public void vtRequestReportGuncelle(int raporID, RequestReportResponse response)
    {
        ReportRequestInfo info = response.getRequestReportResult().getReportRequestInfo();

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

    /**
     * Request Report request sample requests the generation of a report
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static RequestReportResponse invokeRequestReport(MarketplaceWebService service, RequestReportRequest request)
    {
        RequestReportResponse response = null;

        try
        {
            response = service.requestReport(request);
            //RequestReportResponse response = service.requestReport(request);

            System.out.println("RequestReport Action Response");
            System.out.println("=============================================================================");
            System.out.println();

            System.out.print("    RequestReportResponse");
            System.out.println();
            if (response.isSetRequestReportResult())
            {
                System.out.print("        RequestReportResult");
                System.out.println();
                RequestReportResult requestReportResult = response.getRequestReportResult();
                if (requestReportResult.isSetReportRequestInfo())
                {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
                    ReportRequestInfo reportRequestInfo = requestReportResult.getReportRequestInfo();
                    if (reportRequestInfo.isSetReportRequestId())
                    {
                        System.out.print("                ReportRequestId");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportRequestId());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportType())
                    {
                        System.out.print("                ReportType");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportType());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetStartDate())
                    {
                        System.out.print("                StartDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getStartDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetEndDate())
                    {
                        System.out.print("                EndDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getEndDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetSubmittedDate())
                    {
                        System.out.print("                SubmittedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getSubmittedDate());
                        System.out.println();
                    }
                    if (reportRequestInfo.isSetReportProcessingStatus())
                    {
                        System.out.print("                ReportProcessingStatus");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getReportProcessingStatus());
                        System.out.println();
                    }
                }
            }
            if (response.isSetResponseMetadata())
            {
                System.out.print("        ResponseMetadata");
                System.out.println();
                ResponseMetadata responseMetadata = response.getResponseMetadata();
                if (responseMetadata.isSetRequestId())
                {
                    System.out.print("            RequestId");
                    System.out.println();
                    System.out.print("                " + responseMetadata.getRequestId());
                    System.out.println();
                }
            }
            System.out.println();
            System.out.println(response.getResponseHeaderMetadata());
            System.out.println();

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
        return response;
    }
}
