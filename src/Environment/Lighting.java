package Environment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class Lighting {
    GamePanel gamePanel;
    BufferedImage darknessFilter;
    float filterAlpha = 0.0f;

    // DAY STATE
    final int day = 0;
    final int night = 1;
    final int dusk = 2;
    final int dawn = 3;
    int dayState = day;

    public Lighting(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setLightSource();
    }

    public void setLightSource() {
        // create bufferedImage
        darknessFilter = new BufferedImage(gamePanel.screenWidth, gamePanel.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) darknessFilter.getGraphics();

        if (gamePanel.listSim.get(gamePanel.indexCurrentSim).getCurrentLight() == null) {
            g2d.setColor(new Color(0, 0, 0, 0.8f));
        } else {
            // get the center x and y of the light circle
            int centerX = gamePanel.listSim.get(gamePanel.indexCurrentSim).screenX + gamePanel.tileSize / 2;
            int centerY = gamePanel.listSim.get(gamePanel.indexCurrentSim).screenY + gamePanel.tileSize / 2;

            // create a gradation effect within the light circle
            Color color[] = new Color[10];
            float fraction[] = new float[10];

            color[0] = new Color(0, 0, 0, 0.0f);
            color[1] = new Color(0, 0, 0, 0.1f);
            color[2] = new Color(0, 0, 0, 0.2f);
            color[3] = new Color(0, 0, 0, 0.3f);
            color[4] = new Color(0, 0, 0, 0.4f);
            color[5] = new Color(0, 0, 0, 0.5f);
            color[6] = new Color(0, 0, 0, 0.6f);
            color[7] = new Color(0, 0, 0, 0.7f);
            color[8] = new Color(0, 0, 0, 0.8f);
            color[9] = new Color(0, 0, 0, 0.9f);

            fraction[0] = 0.0f;
            fraction[1] = 0.1f;
            fraction[2] = 0.2f;
            fraction[3] = 0.3f;
            fraction[4] = 0.4f;
            fraction[5] = 0.5f;
            fraction[6] = 0.6f;
            fraction[7] = 0.7f;
            fraction[8] = 0.8f;
            fraction[9] = 0.9f;

            // create a gradation paint settings for the light circle
            RadialGradientPaint gPaint = new RadialGradientPaint(centerX, centerY, 500 / 2, fraction, color);

            // set the gradient data on g2d
            g2d.setPaint(gPaint);
        }

        g2d.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);

        g2d.dispose();
    }

    public void update() {
        // check the state of the day
        int time = gamePanel.worldTimeCounter%720;

        if (dayState == day) {
            if (time == 360) { 
                dayState = dusk;
            } else if (time < 360){
                dayState = day;
                filterAlpha = 0.0f;
            } else {
                dayState = night;
                filterAlpha = 0.9f;
            }
        }

        if (dayState == dusk) {
            filterAlpha += 0.01f;
            if (filterAlpha > 0.9f) {
                filterAlpha = 0.9f;
                dayState = night;
            }
        }

        if (dayState == night) {
            if (time == 0) {
                dayState = dawn;
            } else if (time < 720 && time >= 360) {
                dayState = night;
                filterAlpha = 0.9f;
            } else {
                dayState = day;
                filterAlpha = 0.0f;
            }
        }

        if (dayState == dawn) {
            filterAlpha -= 0.01f;
            if (filterAlpha < 0.0f) {
                filterAlpha = 0.0f;
                dayState = day;
            }
        }

        // update the light source
        if (gamePanel.listSim.get(gamePanel.indexCurrentSim).getLightUpdated()) {
            setLightSource();
            gamePanel.listSim.get(gamePanel.indexCurrentSim).setLightUpdated(false);
        }
    }

    public void draw(Graphics2D g2d) {
        if (gamePanel.getCurrentSim().getCurrentMap() == 0){
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, filterAlpha)); // set the alpha value of the filter
            g2d.drawImage(darknessFilter, 0, 0, null); // draw the filter
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset the alpha value

        String situation = "";
        switch (dayState) {
            case day:
                situation = "day";
                break;
            case night:
                situation = "night";
                break;
            case dusk:
                situation = "dusk";
                break;
            case dawn:
                situation = "dawn";
                break;
        }
        g2d.setColor(Color.WHITE);
        g2d.setFont(g2d.getFont().deriveFont(20f));
        g2d.drawString(situation, 700, 700);

    }
}
