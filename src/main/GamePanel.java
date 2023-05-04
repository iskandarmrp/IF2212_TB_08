package main;

import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import benda.Benda;
import entity.*;
import Environment.EnvironmentManager;
import map.TileManager;
import map.Map;
import data.*;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3; // 3x scale

    public final int tileSize = originalTileSize * scale; // 48x48 tile (1 kotak)
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 16;
    public final int screenWidth = maxScreenCol * tileSize; // 768 pixels
    public final int screenHeight = maxScreenRow * tileSize; // 768 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 66;
    public final int maxWorldRow = 66;
    public final int worldWidth = maxWorldCol * tileSize; // 3168 pixels
    public final int worldHeight = maxWorldRow * tileSize; // 3168 pixels
    public int worldTimeCounter = 0; // Time : worldTimeCounter%720 ; Day : worldTimeCounter/720


    // darr ini gua tambah lagi
    public int worldTimeSatuHariCounter = 0; // Time : worldTimeCounter%720 ; Day : worldTimeCounter/720

    // MAP SETTINGS
    public final int maxMap = 2; // Ruangan and world
    // public int listSim.get(indexCurrentSim).currentMap = 0;

    // FPS
    int fps = 60; // frames per second

    // SYSTEM
    public TileManager tileManager = new TileManager(this); // create a new TileManager object
    public KeyHandler keyHandler = new KeyHandler(this); // create a new KeyHandler object
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    AssetSetter assetSetter = new AssetSetter(this); // create a new AssetSetter object
    public CollisionChecker collisionChecker = new CollisionChecker(this); // create a new CollisionChecker object
    public UI ui = new UI(this); // create a new UI object
    public EventHandler eventHandler = new EventHandler(this); // create a new EventHandler object
    EnvironmentManager environmentManager = new EnvironmentManager(this); // create a new EnvironmentManager object
    Map map = new Map(this);
    SaveLoad saveLoad = new SaveLoad(this);
    public CutsceneManager cutsceneManager = new CutsceneManager(this);
    Thread gameThread; // thread for the game

    // Multisim
    public List<Sim> listSim = new ArrayList<>();
    public int indexCurrentSim;
    public boolean isOneSim = true;
    // public Sim currentSim = listSim.get(indexCurrentSim);

    // ENTITY
    // public Sim sim = new Sim(this, keyHandler);
    public Entity npc[][] = new Entity[maxMap][6]; // create an array of NPC objects
    public Entity kokiTemp = new NPC_Koki(this);

    // BENDA
    // @SuppressWarnings("unchecked")
    @SuppressWarnings("unchecked")
    public List<Benda>[] listRumah = new ArrayList[maxMap]; // create an array of ArrayList of Integer objects

    // public Benda rumah[][] = new Benda[maxMap][8]; // create an array of Benda
    // objects yang dapat diletakkan

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogState = 3;
    public final int simInfoState = 4;
    public final int inventoryState = 5;
    public final int beliState = 6;
    public final int upgradeRumahState = 7;
    public final int inputNamaRuanganState = 8;
    public final int inputKoordinatBendaState = 9;
    public final int addSimState = 10;
    public final int inputKoordinatRumahSimState = 11;
    public final int changeSimState = 12;
    public final int menuState = 13;
    public final int helpState = 14;
    public final int mapState = 15;
    public final int resepState = 16;
    public final int timerState = 17;
    public final int inputDurasiTidurState = 18;
    public final int gameOverState = 19;
    public final int inputDurasiNontonState = 20;
    public final int inputDurasiMandiState = 21;
    public final int inputDurasiShalatState = 22;
    public final int inputDurasiBacaBukuState = 23;
    public final int inputDurasiRadioState = 24;
    public final int inputDurasiSiramTanamanState = 25;
    public final int inputDurasiMainGameState = 26;
    public final int kerjaState = 27;
    public final int inputDurasiKerjaState = 28;
    public final int gantiPekerjaanState = 29;
    public final int inputDurasiOlahragaState = 30;
    public final int saveState = 31;
    public final int melihatWaktuState = 32;
    public final int cutsceneState = 33;

    public GamePanel() {
        for (int i = 0; i < maxMap; i++) {
            listRumah[i] = new ArrayList<Benda>();
        }
        listSim.add(new Sim(this, keyHandler)); // nambah sim pertama

        indexCurrentSim = 0;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // set the size of the panel
        this.setBackground(Color.black); // set the background color of the panel
        this.setDoubleBuffered(true); // set the panel to be double buffered
        this.addKeyListener(keyHandler); // add the key listener to the panel
        this.setFocusable(true); // set the panel to be focusable (so that it can receive key events)
    }

    public void setupGame() {
        gameState = titleState; // set the default game state to titleState
        assetSetter.setBenda(); // setup the benda
        assetSetter.setNPC(); // setup the assets
        environmentManager.setup(); // setup the environment
        playMusic(0);
        soundEffect.setVolume(2);
    }

    public void startGameThread() { // start the game thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() { // the game loop
        double drawInterval = 1000000000.0 / fps; // 0.0167 seconds
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {

        

        for (int i = 0; i < listSim.size(); i++) {
            if (listSim.get(i).getPekerjaan().worldTimeCounterForStartJobAfterChangeJob >= 720) {
                listSim.get(i).getPekerjaan().isCanStartPekerjaan = true;
                listSim.get(i).getPekerjaan().worldTimeCounterForStartJobAfterChangeJob = 0;
                
            }

            if (worldTimeSatuHariCounter >= 720) {
                worldTimeSatuHariCounter = 0;
                listSim.get(i).setEfekWaktuTidakTidurCounter(0);
                listSim.get(i).setEfekWaktuTidakBuangAirCounter(0);
                listSim.get(i).setIsUdahMakanDalamSatuHari(false);
            }

            if (listSim.get(i).getPekerjaan().totalDurasiKerjaPerPekerjaan >= 720) {
                listSim.get(i).getPekerjaan().isCanChangePekerjaan = true;
            } else {
                listSim.get(i).getPekerjaan().isCanChangePekerjaan = false;   
            }

            if (listSim.get(i).getEfekWaktuTidakTidurCounter() >= 600){
                // kurangin kesejahteraan
                listSim.get(i).setEfekWaktuTidakTidurCounter(0);
                listSim.get(i).setMood(listSim.get(i).getMood() - 5);
                listSim.get(i).setKesehatan(listSim.get(i).getKesehatan() - 5);
                ui.addMessage("-5 mood, -5 kesehatan");
            }

            if (listSim.get(i).getEfekWaktuTidakBuangAirCounter() >= 240){
                // kurangin kesejahteraan
                if (listSim.get(i).getIsUdahMakanDalamSatuHari()){
                    listSim.get(i).setEfekWaktuTidakBuangAirCounter(0);
                    listSim.get(i).setMood(listSim.get(i).getMood() - 5);
                    listSim.get(i).setKesehatan(listSim.get(i).getKesehatan() - 5);
                    ui.addMessage("-5 mood, -5 kesehatan");
                }
            }
        }

        // for (int i = 0; i < listSim.size(); i++) {
            
        // }

        if (gameState == playState) {
            listSim.get(indexCurrentSim).update();
            for (int i = 0; i < npc[listSim.get(indexCurrentSim).getCurrentMap()].length; i++) {
                if (npc[listSim.get(indexCurrentSim).getCurrentMap()][i] != null) {
                    npc[listSim.get(indexCurrentSim).getCurrentMap()][i].update();
                }
            }
        } else if (gameState == pauseState) {
            // do nothing
        } else if (gameState == dialogState) {
            // do nothing
        }

        // memastikan kalau sim berada di world indexRuangan 999
        if (listSim.get(indexCurrentSim).getCurrentMap() == 0) {
            listSim.get(indexCurrentSim).setIndexLocationRuangan(999);
            listSim.get(indexCurrentSim).setCurrentLocation("World");
        }
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSoundEffect(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }

    public Sim getCurrentSim() {
        return listSim.get(indexCurrentSim);
    }

    public Map getMap() {
        return map;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // call the paintComponent method of the parent class

        Graphics2D g2d = (Graphics2D) g; // cast the Graphics object to Graphics2D

        if (gameState == titleState) {
            ui.draw(g2d); // draw the title screen
        } else {
            // draw the background
            if (listSim.get(indexCurrentSim).getCurrentMap() == 0) {
                tileManager.draw(g2d, 999);
            } else {
                tileManager.draw(g2d, listSim.get(indexCurrentSim).getIndexLocationRuangan());
            }

            // draw benda
            if (listSim.get(indexCurrentSim).getCurrentMap() == 0) {
                for (int i = 0; i < listRumah[listSim.get(indexCurrentSim).getCurrentMap()].size(); i++) {
                    if (listRumah[listSim.get(indexCurrentSim).getCurrentMap()].get(i) != null) {
                        listRumah[listSim.get(indexCurrentSim).getCurrentMap()].get(i).draw(g2d, this);
                    }
                }
            } else {
                for (int i = 0; i < listSim.get(listSim.get(indexCurrentSim).getIndexRumahYangDimasuki()).getRumah().getRuanganRumah()
                        .get(listSim.get(indexCurrentSim).getIndexLocationRuangan()).getBendaRuangan().size(); i++) {
                    if (listSim.get(listSim.get(indexCurrentSim).getIndexRumahYangDimasuki()).getRumah().getRuanganRumah()
                            .get(listSim.get(indexCurrentSim).getIndexLocationRuangan()).getBendaRuangan().get(i) != null) {
                        listSim.get(listSim.get(indexCurrentSim).getIndexRumahYangDimasuki()).getRumah().getRuanganRumah()
                                .get(listSim.get(indexCurrentSim).getIndexLocationRuangan()).getBendaRuangan().get(i)
                                .draw(g2d, this);
                    }
                }
            }

            // draw npc
            for (int i = 0; i < npc[listSim.get(indexCurrentSim).getCurrentMap()].length; i++) {
                if (npc[listSim.get(indexCurrentSim).getCurrentMap()][i] != null) {
                    npc[listSim.get(indexCurrentSim).getCurrentMap()][i].draw(g2d);
                }
            }

            // draw sim
            listSim.get(indexCurrentSim).draw(g2d);

            // draw environment
            environmentManager.update();
            environmentManager.draw(g2d);

            // // draw mini map
            // map.drawMiniMap(g2d);

            // draw ui
            ui.draw(g2d);

            // world time
            if (keyHandler.checkWorldTime) {
                g2d.setColor(Color.white);
                g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN,40f));
                g2d.drawString("World Time: " + worldTimeCounter % 720, 10, 700);
                g2d.drawString("World Day: " + worldTimeCounter / 720, 10, 748);
            }

            // current location
            if (keyHandler.checkCurrentLocation) {
                g2d.setColor(Color.white);
                g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN,40f));
                g2d.drawString("Current Location: " + listSim.get(indexCurrentSim).getCurrentLocation(), 10, 700);
            }

        }

        g2d.dispose(); // dispose the Graphics2D object, freeing up memory
    }
}
