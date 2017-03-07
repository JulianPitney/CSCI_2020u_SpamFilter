import java.io.File;
import java.text.DecimalFormat;


public class TestFile {

    private String fileName;
    private double spamProbability;
    private String actualClass;
    private File actualFile;



    public TestFile(String fileName, double spamProbability, String actualClass, File actualFile)
    {
        this.fileName = fileName;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
        this.actualFile = actualFile;
    }


    public String getFileName()
    {
        return this.fileName;
    }

    public String getSpamProbability()
    {
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }

    public String getActualClass()
    {
        return this.actualClass;
    }

    public File getActualFile () {return this.actualFile;}

    public void setFilename(String value)
    {
        this.fileName = value;
    }

    public void setSpamProbability(double val)
    {
        this.spamProbability = val;
    }

    public void setActualClass(String value)
    {
        this.actualClass = value;
    }

    public void setActualFile (File actualFile) { this.actualFile = actualFile;}
}
