package amws;

public class RaporIstek
{

    private int id;
    private String baslangicTarihi;
    private String bitisTarihi;
    private String tip;

    public RaporIstek(int id, String baslangicTarihi, String bitisTarihi, String tip)
    {
        this.id = id;
        this.baslangicTarihi = baslangicTarihi;
        this.bitisTarihi = bitisTarihi;
        this.tip = tip;
    }

    public int getId()
    {
        return id;
    }

    public String getBaslangicTarihi()
    {
        return baslangicTarihi;
    }

    public String getBitisTarihi()
    {
        return bitisTarihi;
    }

    public String getTip()
    {
        return tip;
    }
}
