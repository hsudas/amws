package amws_report;

public class Rapor
{

    private int id;
    private String reportRequestID;
    private String generatedReportID;

    public Rapor(int id, String reportRequestID)
    {
        this.id = id;
        this.reportRequestID = reportRequestID;
    }

    public String getGeneratedReportID()
    {
        return generatedReportID;
    }

    public int getId()
    {
        return id;
    }

    public String getReportRequestID()
    {
        return reportRequestID;
    }
}
