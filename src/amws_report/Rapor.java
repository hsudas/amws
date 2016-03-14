package amws_report;

public class Rapor
{

    private int id;
    private String reportRequestID;
    private String generatedReportID;
    private String uuid;

    public Rapor(int id, String reportRequestID, String uuid)
    {
        this.id = id;
        this.reportRequestID = reportRequestID;
        this.uuid = uuid;
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

    public String getUuid()
    {
        return uuid;
    }
}
