/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1006099
 */
public class Monde extends Observable implements Runnable {

    protected int size;
    protected Cellule[][] grille;
    protected Regles regle;
    protected ArrayList<Coordonnee> changement;
    protected boolean useteams;
    protected Set<Integer> teams;
    // Données nécéssaires au threading
    protected int threadSpeed;
    protected boolean pauseThreadFlag;

    // Les constructeurs
    public Monde(int size) {
        this.size = size;
        this.threadSpeed = 1500;

        this.regle = new Regles(3, 2, 3);
        grille = new Cellule[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule(0);
            }
        }

        changement = new ArrayList<>();

        pauseThreadFlag = false;
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
        this.threadSpeed = 1500;

        this.regle = new Regles(reveil, survie, mort);
        grille = new Cellule[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule(0);
            }
        }

        changement = new ArrayList<>();

        pauseThreadFlag = false;
    }

    public Monde(int size, int reveil, int survie, int mort, int nbteams) {
        this.size = size;
        this.useteams = true;

        this.regle = new Regles(reveil, survie, mort);
        grille = new Cellule[size][size];

        for (int i = 0; i < nbteams; i++) {
            teams.add(new Integer(i));
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule((int) Math.round((Math.random() * nbteams) + 1));
            }
        }

        changement = new ArrayList<>();
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

    public void init(int size) {
        this.size = size;
        this.grille = new Cellule[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule(0);
            }
        }

        this.random();
        System.out.println("Initialisation monde");

        this.notifyObservers();
    }

    public void init(int size, int taux) {
        this.size = size;
        this.grille = new Cellule[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule(0);
            }
        }

        this.random(taux);
        System.out.println("Initialisation monde");

        this.notifyObservers();
    }

    public void update() {
        int alive;
        changement.clear();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                alive = 0;

                for (int x = -1; x < 2; x++) {
                    for (int y = - 1; y < 2; y++) {
                        if ((i + x >= 0 && j + y >= 0) && (i + x < size && j + y < size)) {
                            if (!(x == 0 && y == 0)) {
                                if (grille[i + x][j + y].isAlive()) {
                                    alive++;
                                }
                            }
                        }
                    }
                }
                if ((!grille[i][j].isAlive() && alive == regle.reveil) || (grille[i][j].isAlive() && (alive < regle.survie || alive > regle.mort))) {
                    changement.add(new Coordonnee(i, j));
                }
            }
        }

        Iterator<Coordonnee> it_coord = changement.iterator();
        while (it_coord.hasNext()) {
            Coordonnee temp = it_coord.next();
            grille[temp.getX()][temp.getY()].setAlive(!grille[temp.getX()][temp.getY()].isAlive());
        }

        this.setChanged();
    }

    public void random() {
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j].alive = rand.nextBoolean();

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
            }
        }

        this.setChanged();
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

    public void stop() {
    }

    public void empty() {
        for (Cellule[] cell : this.grille) {
            for (Cellule cell2 : cell) {
                cell2.setAlive(false);
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
}
