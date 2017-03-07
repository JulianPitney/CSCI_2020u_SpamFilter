import java.io.File;
import java.text.DecimalFormat;


public class TestFile {

    private String filename;
    private double spamProbability;
    private String actualClass;
    private File actualFile;



    public TestFile(String filename, double spamProbability, String actualClass, File actualFile)
    {
        this.filename = filename;
        this.spamProbability = spamProbability;
        this.actualClass = actualClass;
        this.actualFile = actualFile;
    }

    public void printInfo()
    {
        System.out.println("FileName= " + this.getFilename());
        System.out.println("FileType= " + this.getActualClass());
        System.out.println("SpamProb= " + this.getSpamProb());
    }

    public String getFilename()
    {
        return this.filename;
    }

    public double getSpamProbability()
    {
        return this.spamProbability;
    }

    public String getSpamProbRounded()
    {
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }

    public Double getSpamProb() {return this.spamProbability;}

    public String getActualClass()
    {
        return this.actualClass;
    }

    public File getActualFile () {return this.actualFile;}

    public void setFilename(String value)
    {
        this.filename = value;
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
