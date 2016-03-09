package amws_report;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static amws_report.Main.dosyayaYaz;

public class Config
{

    private String VT_USERNAME = "";
    private String VT_PASSWORD = "";
    private String VT_IP = "";
    private String VT_DATABASE_NAME = "";
    private String ARCHIVE_FOLDER = "";
    private String FILE_NAME_FORMAT = "";
    private String TABLE_REQUEST = "";
    private String TABLE_CONTENTS = "";
    private String TABLE_SCHEDULE = "";
    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public boolean ayarlariOku()
    {
        dosyayaYaz("ayarlar okunuyor");
        boolean sonuc = true;
        BufferedReader br = null;
        try
        {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("config.txt"));

            while ((sCurrentLine = br.readLine()) != null)
            {
                String[] ayar = sCurrentLine.split("=");
                ayarla(ayar);
            }
        }
        catch (IOException e)
        {
            dosyayaYaz("hata 14 : " + e.getMessage());
            dosyayaYaz("ayarlar okunurken hata olustu");
            sonuc = false;
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ex)
            {
                dosyayaYaz("hata 15 : " + ex.getMessage());
                dosyayaYaz("ayarlar okunurken hata olustu");
                sonuc = false;
            }
        }
        dosyayaYaz("ayarlar okundu");
        return sonuc;
    }

    public void ayarla(String[] ayar)
    {
        switch (ayar[0])
        {
            case "VT_USERNAME":
                setVT_USERNAME(ayar[1]);
                break;
            case "VT_PASSWORD":
                setVT_PASSWORD(ayar[1]);
                break;
            case "VT_DATABASE_NAME":
                setVT_DATABASE_NAME(ayar[1]);
                break;
            case "VT_IP":
                setVT_IP(ayar[1]);
                break;
            case "ARCHIVE_FOLDER":
                setARCHIVE_FOLDER(ayar[1]);
                break;
            case "FILE_NAME_FORMAT":
                setFILE_NAME_FORMAT(ayar[1]);
                break;
            case "TABLE_REQUEST":
                setTABLE_REQUEST(ayar[1]);
                break;
            case "TABLE_CONTENTS":
                setTABLE_CONTENTS(ayar[1]);
                break;
            case "TABLE_SCHEDULE":
                setTABLE_SCHEDULE(ayar[1]);
                break;
            default:
                break;
        }
    }

    public boolean ayarlariKontrolEt()
    {
        if (getARCHIVE_FOLDER().isEmpty())
        {
            dosyayaYaz("ARCHIVE_FOLDER ayarı yok");
            return false;
        }
        else if (getVT_DATABASE_NAME().isEmpty())
        {
            dosyayaYaz("VT_DATABASE_NAME ayarı yok");
            return false;
        }
        else if (getVT_IP().isEmpty())
        {
            dosyayaYaz("VT_IP ayarı yok");
            return false;
        }
        else if (getVT_PASSWORD().isEmpty())
        {
            dosyayaYaz("VT_PASSWORD ayarı yok");
            return false;
        }
        else if (getVT_USERNAME().isEmpty())
        {
            dosyayaYaz("VT_USERNAME ayarı yok");
            return false;
        }
        else if (getFILE_NAME_FORMAT().isEmpty())
        {
            dosyayaYaz("FILE_NAME_FORMAT ayarı yok");
            return false;
        }
        else if (getTABLE_REQUEST().isEmpty())
        {
            dosyayaYaz("TABLE_REQUEST ayarı yok");
            return false;
        }
        else if (getTABLE_CONTENTS().isEmpty())
        {
            dosyayaYaz("TABLE_CONTENTS ayarı yok");
            return false;
        }
        else if (getTABLE_SCHEDULE().isEmpty())
        {
            dosyayaYaz("TABLE_SCHEDULE ayarı yok");
            return false;
        }
        return true;
    }

    public String getVT_USERNAME()
    {
        return VT_USERNAME;
    }

    public void setVT_USERNAME(String VT_USERNAME)
    {
        this.VT_USERNAME = VT_USERNAME;
    }

    public String getVT_PASSWORD()
    {
        return VT_PASSWORD;
    }

    public void setVT_PASSWORD(String VT_PASSWORD)
    {
        this.VT_PASSWORD = VT_PASSWORD;
    }

    public String getVT_IP()
    {
        return VT_IP;
    }

    public void setVT_IP(String VT_IP)
    {
        this.VT_IP = VT_IP;
    }

    public String getVT_DATABASE_NAME()
    {
        return VT_DATABASE_NAME;
    }

    public void setVT_DATABASE_NAME(String VT_DATABASE_NAME)
    {
        this.VT_DATABASE_NAME = VT_DATABASE_NAME;
    }

    public String getARCHIVE_FOLDER()
    {
        return ARCHIVE_FOLDER;
    }

    public void setARCHIVE_FOLDER(String ARCHIVE_FOLDER)
    {
        this.ARCHIVE_FOLDER = ARCHIVE_FOLDER;
    }

    public String getFILE_NAME_FORMAT()
    {
        return FILE_NAME_FORMAT;
    }

    public void setFILE_NAME_FORMAT(String FILE_NAME_FORMAT)
    {
        this.FILE_NAME_FORMAT = FILE_NAME_FORMAT;
    }

    public String getTABLE_REQUEST()
    {
        return TABLE_REQUEST;
    }

    public void setTABLE_REQUEST(String TABLE_REQUEST)
    {
        this.TABLE_REQUEST = TABLE_REQUEST;
    }

    public String getTABLE_CONTENTS()
    {
        return TABLE_CONTENTS;
    }

    public void setTABLE_CONTENTS(String TABLE_CONTENTS)
    {
        this.TABLE_CONTENTS = TABLE_CONTENTS;
    }

    public String getTABLE_SCHEDULE()
    {
        return TABLE_SCHEDULE;
    }

    public void setTABLE_SCHEDULE(String TABLE_SCHEDULE)
    {
        this.TABLE_SCHEDULE = TABLE_SCHEDULE;
    }

    public String getDATE_FORMAT()
    {
        return DATE_FORMAT;
    }
}
