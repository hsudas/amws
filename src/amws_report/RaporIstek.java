package amws_report;

public class RaporIstek
{

    private int id;
    private String reportRequestID;

    public RaporIstek(int id, String reportRequestID)
    {
        this.id = id;
        this.reportRequestID = reportRequestID;
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
