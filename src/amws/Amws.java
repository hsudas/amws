package amws;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.GetReportRequestListRequest;
import com.amazonaws.mws.model.GetReportRequestListResponse;
import com.amazonaws.mws.model.GetReportRequestListResult;
import com.amazonaws.mws.model.ReportRequestInfo;
import com.amazonaws.mws.model.ResponseMetadata;

public class Amws
{

    public static void main(String[] args)
    {
        VTThread vtThread = new VTThread();
        vtThread.start();
    }

    public static void getReportRequestList()
    {
        /*
        GetReportRequestListRequest request = new GetReportRequestListRequest();
        request.setMerchant(merchantId);

        IdList liste = new IdList(new ArrayList<>(Arrays.asList("264012016838")));

        System.out.println("idlist : " + liste.getId().toString());

        request.setReportRequestIdList(liste);

        invokeGetReportRequestList(service, request);
         */
    }

    /**
     * Get Report Request List request sample returns a list of report requests
     * ids and their associated metadata
     *
     * @param service instance of MarketplaceWebService service
     * @param request Action to invoke
     */
    public static void invokeGetReportRequestList(MarketplaceWebService service, GetReportRequestListRequest request)
    {
        try
        {

            GetReportRequestListResponse response = service.getReportRequestList(request);

            System.out.println("GetReportRequestList Action Response");
            System.out.println("=============================================================================");
            System.out.println();

            System.out.print("    GetReportRequestListResponse");
            System.out.println();
            if (response.isSetGetReportRequestListResult())
            {
                System.out.print("        GetReportRequestListResult");
                System.out.println();
                GetReportRequestListResult getReportRequestListResult = response.getGetReportRequestListResult();
                if (getReportRequestListResult.isSetNextToken())
                {
                    System.out.print("            NextToken");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.getNextToken());
                    System.out.println();
                }
                if (getReportRequestListResult.isSetHasNext())
                {
                    System.out.print("            HasNext");
                    System.out.println();
                    System.out.print("                " + getReportRequestListResult.isHasNext());
                    System.out.println();
                }
                java.util.List<ReportRequestInfo> reportRequestInfoList = getReportRequestListResult.getReportRequestInfoList();
                for (ReportRequestInfo reportRequestInfo : reportRequestInfoList)
                {
                    System.out.print("            ReportRequestInfo");
                    System.out.println();
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
                    if (reportRequestInfo.isSetCompletedDate())
                    {
                        System.out.print("                CompletedDate");
                        System.out.println();
                        System.out.print("                    " + reportRequestInfo.getCompletedDate());
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
    }

}
