package map;

import java.awt.image.*;

public class Tile {
      private BufferedImage image;
      private boolean collision = false;
      
      public BufferedImage getImage() {
            return image;
      }
      public void setImage(BufferedImage image) {
            this.image = image;
      }
      public boolean isCollision() {
            return collision;
      }
      public void setCollision(boolean collision) {
            this.collision = collision;
      }

      
}
