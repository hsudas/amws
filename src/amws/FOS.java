package amws;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class FOS extends FileOutputStream
{

    private final StringBuilder string = new StringBuilder();

    public FOS(String name) throws FileNotFoundException
    {
        super(name);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        string.append(new String(b, off, len));
        super.write(b, off, len); //To change body of generated methods, choose Tools | Templates.
    }

    public String getDosyaIcerigi()
    {
        return string.toString();
    }
}
