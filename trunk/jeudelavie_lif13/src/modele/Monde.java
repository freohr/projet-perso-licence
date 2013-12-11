/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import Utils.GrilleImport;
import Utils.XML;
import static Utils.XML.importPreconstruit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author p1006099
 */
public class Monde extends Observable implements Runnable {

    //Données de base
    protected int size;
    protected Cellule[][] grille;
    protected Regles regle;
    protected ArrayList<Coordonnee> changement;
    protected boolean useteams;
    protected Set<Integer> teams;
    //Données de l'import de motif
    protected Cellule[][] motif;
    protected boolean hasMotif;
    protected int motifSizeX;
    protected int motifSizeY;
    protected int motifOffsetX;
    protected int motifOffsetY;
    // Données nécéssaires au threading
    protected int threadSpeed;
    protected int nbThreads;
    protected boolean pauseThreadFlag;

    // Les constructeurs
    public Monde(int size) {
        this.size = size;
        this.threadSpeed = 200;
        this.nbThreads = 2;

        this.regle = new Regles(3, 2, 3);
        grille = new Cellule[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule();
            }
        }

        changement = new ArrayList<>();

        motif = null;
        hasMotif = false;
        motifOffsetX = 0;
        motifOffsetY = 0;
    }

    /**
     *
     * @param size la taille de la grille représentant l'environnement
     * @param reveil le nombres de celluls voisines vivantes nécéssaires pour
     * réveiller une cellule morte
     * @param survie le nombres de cellules voisines vivantes minimum pour
     * garder une cellule en vie
     * @param mort le nombre de cellules voisines vivantes maximum pour garder
     * une cellule en vie
     */
    public Monde(int size, int reveil, int survie, int mort) {
        this.size = size;
        this.threadSpeed = 200;

        this.nbThreads = 2;

        this.regle = new Regles(reveil, survie, mort);
        grille = new Cellule[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule();
            }
        }

        changement = new ArrayList<>();

        pauseThreadFlag = false;

        motif = null;
        hasMotif = false;
        motifOffsetX = 0;
        motifOffsetY = 0;
    }

    public int getSize() {
        return size;
    }

    public Cellule getCellule(int i, int j) {
        return grille[i][j];
    }

    public ArrayList<Coordonnee> getChangement() {
        return changement;
    }

    public Regles getRegle() {
        return regle;
    }

    public int getThreadSpeed() {
        return threadSpeed;
    }

    public boolean isPaused() {
        return pauseThreadFlag;
    }

    public void setThreadSpeed(int threadSpeed) {
        this.threadSpeed = threadSpeed;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setUseteams(boolean useteams) {
        this.useteams = useteams;
    }

    public int getNbThreads() {
        return nbThreads;
    }

    public void setNbThreads(int nbThreads) {
        this.nbThreads = nbThreads;
    }

    public boolean hasMotif() {
        return hasMotif;
    }

    public void setMotifFlag(boolean hasMotif) {
        this.hasMotif = hasMotif;
    }

    public int getMotifOffsetX() {
        return motifOffsetX;
    }

    public Cellule getMotifCase(int x, int y) {
        return motif[x][y];
    }

    public void setMotifOffsetX(int motifOffsetX) {
        this.motifOffsetX = motifOffsetX;
    }

    public int getMotifOffsetY() {
        return motifOffsetY;
    }

    public void setMotifOffsetY(int motifOffsetY) {
        this.motifOffsetY = motifOffsetY;
    }

    public int getMotifSizeX() {
        return motifSizeX;
    }

    public void setMotifSizeX(int motifSizeX) {
        this.motifSizeX = motifSizeX;
    }

    public int getMotifSizeY() {
        return motifSizeY;
    }

    public void setMotifSizeY(int motifSizeY) {
        this.motifSizeY = motifSizeY;
    }

    //Initialisation de la grille
    synchronized public void initEmpty(int size) {
        this.size = size;
        this.grille = new Cellule[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule();
            }
        }

        this.setChanged();
        this.notifyObservers();

    }

    synchronized public void init(int size) {
        this.size = size;
        this.grille = new Cellule[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule();
            }
        }

        this.random();

        this.notifyObservers();
    }

    synchronized public void init(int size, int taux) {
        this.size = size;
        this.grille = new Cellule[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule();
            }
        }

        this.random(taux);

        this.notifyObservers();
    }

    public void random() {
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j].alive = rand.nextBoolean();
                grille[i][j].setChargeValue(rand.nextInt(3) - 1);
            }
        }

        this.setChanged();
    }

    public void random(int taux) {
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ((Math.round(rand.nextFloat() * 100)) <= taux) {
                    grille[i][j].alive = true;
                }
                grille[i][j].setChargeValue(rand.nextInt() % 3 - 1);
            }
        }

        this.setChanged();
    }

    //Mise à jour de la grille
    public void update() {
        int alive;
        changement.clear();

        if (nbThreads > 1) {
            int minx = 0, maxx = 0, miny = 0, maxy = 0;

            for (int i = 0; i < nbThreads; i++) {
                minx = maxx;

                maxx += size / nbThreads;
                if (i == nbThreads - 1) {
                    maxx = size;
                }

                miny = 0;
                maxy = 0;
                for (int j = 0; j < nbThreads; j++) {
                    miny = maxy;
                    maxy += size / nbThreads;

                    if (j == nbThreads - 1) {
                        maxy = size;
                    }

                    sousGrille tmp = new sousGrille(minx, maxx, miny, maxy, this);
                    tmp.run();
                }
            }

        } else {
            regle.appliquerRegles(this);
        }

        Iterator<Coordonnee> it_coord = changement.iterator();
        while (it_coord.hasNext()) {
            Coordonnee temp = it_coord.next();
            grille[temp.getX()][temp.getY()].setAlive(!grille[temp.getX()][temp.getY()].isAlive());
        }
        this.showMotif();
        this.setChanged();
    }

    public void empty() {
        for (Cellule[] cell : this.grille) {
            for (Cellule cell2 : cell) {
                cell2.setAlive(false);
                cell2.setChargeValue(0);
                cell2.setImmortal(false);
            }
        }

        this.setChanged();
        notifyObservers();
    }

    public void inverseCell(int x, int y) {
        grille[x][y].setAlive(!grille[x][y].isAlive());

        this.setChanged();
        notifyObservers();
    }

    public void setCellAlive(int x, int y) {
        getCellule(x, y).setAlive(true);
        this.setChanged();
        notifyObservers();
    }

    public void setCellDead(int x, int y) {
        getCellule(x, y).setAlive(false);
        this.setChanged();
        notifyObservers();
    }

    //Sauvegarde et chargement
    public void save(String path) {
        try {
            XML.saveGrille(this, path);
        } catch (ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Erreur lors de la sauvegarde du fichier " + path);
        }
    }

    public void loadGrille(GrilleImport chargeGrille) {
        if (chargeGrille.getSizeX() != size || chargeGrille.getSizeY() != size) {
            this.size = chargeGrille.getSizeX();
            this.grille = new Cellule[size][size];

        }

        for (int i = 0; i < size; i++) {
            System.arraycopy(chargeGrille.getGrille()[i], 0, grille[i], 0, size);
        }

        this.setChanged();
        this.notifyObservers();
    }

    //les motifs
    public void importMotif(String motif) {
        try {
            GrilleImport tmp = importPreconstruit(motif);
            setMotifFlag(true);
            this.setMotifOffsetX(0);
            this.setMotifOffsetY(0);
            this.motif = tmp.getGrille();
            this.motifSizeX = tmp.getSizeX();
            this.motifSizeY = tmp.getSizeY();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Erreur d'import du motif \"" + motif + "\" depuis XML");
        }
    }

    public void applyMotif() {
        for (int i = motifOffsetX; i < Math.min(motifOffsetX + motifSizeX, size); i++) {
            for (int j = motifOffsetY; j < Math.min(motifOffsetY + motifSizeY, size); j++) {
                grille[i][j].setAlive(motif[i - motifOffsetX][j - motifOffsetY].isAlive());
            }
        }

        this.setChanged();
        notifyObservers();
    }

    public void emptyMotif() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j].setUnderMotif(false);
            }
        }

        this.setChanged();
        notifyObservers();
    }

    public void showMotif() {
        emptyMotif();

        for (int i = motifOffsetX; i < Math.min(motifOffsetX + motifSizeX, size); i++) {
            for (int j = motifOffsetY; j < Math.min(motifOffsetY + motifSizeY, size); j++) {
                grille[i][j].setUnderMotif(true);
            }
        }

        this.setChanged();
        notifyObservers();

    }

    // Le threading du modèle
    @Override
    public void run() {
        pauseThreadFlag = true;

        random();
        notifyObservers();

        /*try {
         * Thread.sleep(1000);
         * } catch (InterruptedException ex) {
         * Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
         * }*/
        while (true) {

            synchronized (this) {
                if (pauseThreadFlag) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            update();
            //System.out.println("");

            notifyObservers();
            try {
                Thread.sleep(this.threadSpeed);
            } catch (InterruptedException ex) {
                Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    public void pause() {
        pauseThreadFlag = true;
    }

    public synchronized void resume() {
        pauseThreadFlag = false;
        notify();
    }

    private class sousGrille implements Runnable {

        int minx;
        int maxx;
        int miny;
        int maxy;
        Monde world;

        public sousGrille(int minx, int maxx, int miny, int maxy, Monde world) {
            this.minx = minx;
            this.maxx = maxx;
            this.miny = miny;
            this.maxy = maxy;
            this.world = world;
        }

        @Override
        public void run() {
            regle.appliquerRegles(minx, maxx, miny, maxy, world);
            //System.out.println("");
        }
    }
}
