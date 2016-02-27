package amws_report;

public class YeniRaporIstek
{

    private int id;
    private String baslangicTarihi;
    private String bitisTarihi;
    private String tip;

    public YeniRaporIstek(int id, String baslangicTarihi, String bitisTarihi, String tip)
    {
        this.id = id;
        this.baslangicTarihi = baslangicTarihi;
        this.bitisTarihi = bitisTarihi;
        this.tip = tip;
    }

    public YeniRaporIstek()
    {
    }

    public int getId()
    {
        return id;
    }

    public String getBaslangicTarihi()
    {
        return baslangicTarihi;
    }

    public void setBaslangicTarihi(String baslangicTarihi)
    {
        this.baslangicTarihi = baslangicTarihi;
    }

    public String getBitisTarihi()
    {
        return bitisTarihi;
    }

    public void setBitisTarihi(String bitisTarihi)
    {
        this.bitisTarihi = bitisTarihi;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }
}
