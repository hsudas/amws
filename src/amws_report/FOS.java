package amws_report;

import static amws_report.Main.dosyayaYaz;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FOS extends FileOutputStream
{

    private final StringBuilder string = new StringBuilder();

    public FOS(String name) throws FileNotFoundException
    {
        super(name);
    }

    @Override
    public void write(byte[] b, int off, int len)
    {
        try
        {
            string.append(new String(b, off, len));
            
            dosyayaYaz("off : " + off + " - len : " + len + " - b : " + b.length);
            dosyayaYaz("string : " + string.length());

            super.write(b, off, len); //To change body of generated methods, choose Tools | Templates.
        }
        catch (IOException e)
        {
            dosyayaYaz("hata 36 : " + e.getMessage());
            dosyayaYaz("dosya icerigi kaydedilirken hata olustu");
        }
    }

    public String getDosyaIcerigi()
    {
        return string.toString();
    }
}
