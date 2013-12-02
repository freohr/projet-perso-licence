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

        this.regle = new Regles(reveil, survie, mort);
        grille = new Cellule[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j] = new Cellule(0);
            }
        }

        changement = new ArrayList<>();

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

    public Regles getRegle() {
        return regle;
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
                if ((!grille[i][j].isAlive() && alive >= regle.reveil) || (grille[i][j].isAlive() && (alive < regle.survie || alive > regle.mort))) {
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
        System.out.println("Random");
        System.out.println();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grille[i][j].alive = rand.nextBoolean();

            }
        }
        this.setChanged();
    }

    @Override
    public void run() {
        System.out.println("Lancement du modele");
        random();
        notifyObservers();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {

            update();
            //random();
            notifyObservers();
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(Monde.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
