package amws_report;

import java.util.ArrayList;
import java.util.List;

/**
 * rapor turlerinin combobox a eklenmesi için sınıf.
 * combobox ta guzel gozukecek fakat vt ye normal hali kaydedilecek
 */
public class RaporTuru
{
    private List listeRaporTuru;
    private String guzel;
    private String cirkin;

    public RaporTuru(String cirkin, String guzel)
    {
        this.cirkin = cirkin;
        this.guzel = guzel;
    }

    public RaporTuru()
    {
        listeRaporTuruOlustur();
    }

    public void listeRaporTuruOlustur()
    {
        listeRaporTuru = new ArrayList();
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_OPEN_LISTINGS_DATA_", "GET FLAT FILE OPEN LISTINGS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_LISTINGS_DATA_", "GET MERCHANT LISTINGS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_LISTINGS_DATA_BACK_COMPAT_", "GET MERCHANT LISTINGS DATA BACK COMPAT"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_LISTINGS_DATA_LITE_", "GET MERCHANT LISTINGS DATA LITE"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_LISTINGS_DATA_LITER_", "GET MERCHANT LISTINGS DATA LITER"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_CANCELLED_LISTINGS_DATA_", "GET MERCHANT CANCELLED LISTINGS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_CONVERGED_FLAT_FILE_SOLD_LISTINGS_DATA_", "GET CONVERGED FLAT FILE SOLD LISTINGS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_MERCHANT_LISTINGS_DEFECT_DATA_", "GET MERCHANT LISTINGS DEFECT DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ACTIONABLE_ORDER_DATA_", "GET FLAT FILE ACTIONABLE ORDER DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_ORDERS_DATA_", "GET ORDERS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ORDERS_DATA_", "GET FLAT FILE ORDERS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_CONVERGED_FLAT_FILE_ORDER_REPORT_DATA_", "GET CONVERGED FLAT FILE ORDER REPORT DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE_", "GET FLAT FILE ALL ORDERS DATA BY LAST UPDATE"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_ORDER_DATE_", "GET FLAT FILE ALL ORDERS DATA BY ORDER DATE"));
        listeRaporTuru.add(new RaporTuru("_GET_XML_ALL_ORDERS_DATA_BY_LAST_UPDATE_", "GET XML ALL ORDERS DATA BY LAST UPDATE"));
        listeRaporTuru.add(new RaporTuru("_GET_XML_ALL_ORDERS_DATA_BY_ORDER_DATE_", "GET XML ALL ORDERS DATA BY ORDER DATE"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_PENDING_ORDERS_DATA_", "GET FLAT FILE PENDING ORDERS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_PENDING_ORDERS_DATA_", "GET PENDING ORDERS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_CONVERGED_FLAT_FILE_PENDING_ORDERS_DATA_", "GET CONVERGED FLAT FILE PENDING ORDERS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_SELLER_FEEDBACK_DATA_", "GET SELLER FEEDBACK DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_V1_SELLER_PERFORMANCE_REPORT_", "GET V1 SELLER PERFORMANCE REPORT"));
        listeRaporTuru.add(new RaporTuru("_GET_V2_SETTLEMENT_REPORT_DATA_FLAT_FILE_", "GET V2 SETTLEMENT REPORT DATA FLAT FILE"));
        listeRaporTuru.add(new RaporTuru("_GET_V2_SETTLEMENT_REPORT_DATA_XML_", "GET V2 SETTLEMENT REPORT DATA XML"));
        listeRaporTuru.add(new RaporTuru("_GET_V2_SETTLEMENT_REPORT_DATA_FLAT_FILE_V2_", "GET V2 SETTLEMENT REPORT DATA FLAT FILE V2"));
        listeRaporTuru.add(new RaporTuru("_GET_AMAZON_FULFILLED_SHIPMENTS_DATA_", "GET AMAZON FULFILLED SHIPMENTS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_LAST_UPDATE_", "GET FLAT FILE ALL ORDERS DATA BY LAST UPDATE"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_ALL_ORDERS_DATA_BY_ORDER_DATE_", "GET FLAT FILE ALL ORDERS DATA BY ORDER DATE"));
        listeRaporTuru.add(new RaporTuru("_GET_XML_ALL_ORDERS_DATA_BY_LAST_UPDATE_", "GET XML ALL ORDERS DATA BY LAST UPDATE"));
        listeRaporTuru.add(new RaporTuru("_GET_XML_ALL_ORDERS_DATA_BY_ORDER_DATE_", "GET XML ALL ORDERS DATA BY ORDER DATE"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_SALES_DATA_", "GET FBA FULFILLMENT CUSTOMER SHIPMENT SALES DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_SALES_DATA_", "GET FBA FULFILLMENT CUSTOMER SHIPMENT SALES DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_PROMOTION_DATA_", "GET FBA FULFILLMENT CUSTOMER SHIPMENT PROMOTION DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_TAXES_DATA_", "GET FBA FULFILLMENT CUSTOMER TAXES DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_AFN_INVENTORY_DATA_", "GET AFN INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_AFN_INVENTORY_DATA_BY_COUNTRY_", "_GET AFN INVENTORY DATA BY COUNTRY"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CURRENT_INVENTORY_DATA_", "GET FBA FULFILLMENT CURRENT INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_MONTHLY_INVENTORY_DATA_", "GET FBA FULFILLMENT MONTHLY INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_INVENTORY_RECEIPTS_DATA_", "GET FBA FULFILLMENT INVENTORY RECEIPTS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_RESERVED_INVENTORY_DATA_", "GET RESERVED INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_INVENTORY_SUMMARY_DATA_", "GET FBA FULFILLMENT INVENTORY SUMMARY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_INVENTORY_ADJUSTMENTS_DATA_", "GET FBA FULFILLMENT INVENTORY ADJUSTMENTS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_INVENTORY_HEALTH_DATA_", "GET FBA FULFILLMENT INVENTORY HEALTH DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_MYI_UNSUPPRESSED_INVENTORY_DATA_", "GET FBA MYI UNSUPPRESSED INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_MYI_ALL_INVENTORY_DATA_", "GET FBA MYI ALL INVENTORY DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CROSS_BORDER_INVENTORY_MOVEMENT_DATA_", "GET FBA FULFILLMENT CROSS BORDER INVENTORY MOVEMENT DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_INBOUND_NONCOMPLIANCE_DATA_", "GET FBA FULFILLMENT INBOUND NONCOMPLIANCE DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_ESTIMATED_FBA_FEES_TXT_DATA_", "GET FBA ESTIMATED FBA FEES TXT DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_REIMBURSEMENTS_DATA_", "GET FBA REIMBURSEMENTS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_RETURNS_DATA_", "GET FBA FULFILLMENT CUSTOMER RETURNS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_CUSTOMER_SHIPMENT_REPLACEMENT_DATA_", "GET FBA FULFILLMENT CUSTOMER SHIPMENT REPLACEMENT DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_RECOMMENDED_REMOVAL_DATA_", "GET FBA RECOMMENDED REMOVAL DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_REMOVAL_ORDER_DETAIL_DATA_", "GET FBA FULFILLMENT REMOVAL ORDER DETAIL DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_FBA_FULFILLMENT_REMOVAL_SHIPMENT_DETAIL_DATA_", "GET FBA FULFILLMENT REMOVAL SHIPMENT DETAIL DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_NEMO_MERCHANT_LISTINGS_DATA_", "GET NEMO MERCHANT LISTINGS DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_DAILY_DATA_TSV_", "GET PADS PRODUCT PERFORMANCE OVER TIME DAILY DATA TSV"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_DAILY_DATA_XML_", "GET PADS PRODUCT PERFORMANCE OVER TIME DAILY DATA XML"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_WEEKLY_DATA_TSV_", "GET PADS PRODUCT PERFORMANCE OVER TIME WEEKLY DATA TSV"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_WEEKLY_DATA_XML_", "GET PADS PRODUCT PERFORMANCE OVER TIME WEEKLY DATA XML"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_MONTHLY_DATA_TSV_", "GET PADS PRODUCT PERFORMANCE OVER TIME MONTHLY DATA TSV"));
        listeRaporTuru.add(new RaporTuru("_GET_PADS_PRODUCT_PERFORMANCE_OVER_TIME_MONTHLY_DATA_XML_", "GET PADS PRODUCT PERFORMANCE OVER TIME MONTHLY DATA XML"));
        listeRaporTuru.add(new RaporTuru("_GET_FLAT_FILE_SALES_TAX_DATA_", "GET FLAT FILE SALES TAX DATA"));
        listeRaporTuru.add(new RaporTuru("_GET_WEBSTORE_PRODUCT_CATALOG_", "GET WEBSTORE PRODUCT CATALOG"));
        listeRaporTuru.add(new RaporTuru("_GET_XML_BROWSE_TREE_DATA_", "GET XML BROWSE TREE DATA"));
    }

    @Override
    public String toString()
    {
        return guzel;
    }

    public List getListeRaporTuru()
    {
        return listeRaporTuru;
    }

    public String getCirkin()
    {
        return cirkin;
    }
}
