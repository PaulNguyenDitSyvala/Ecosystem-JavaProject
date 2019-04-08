import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;


public class Night{
  private boolean isNight;
  private Image img;

  public Night(String img){
    try
    {
      this.img = ImageIO.read(new File("Images/"+img+".png"));
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    isNight = false;
  }

  public Image getImage(){
    return img;
  }

  public boolean isNight(){
    return isNight;
  }

  public void sunset(){
    isNight = true;
  }

  public void sunrise(){
    isNight = false;
  }
}
