/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author p1006099
 */
public class Regles {

    protected int reveil;
    protected int survie;
    protected int mort;
    protected boolean useSuperCells;
    protected int nbGenSuperCells;
    protected boolean usePoseNeg;
    protected Map<Coordonnee, Integer> changeCharge;

    public Regles() {
        this.useSuperCells = false;

        this.reveil = 3;
        this.survie = 2;
        this.mort = 3;
        this.changeCharge = new HashMap<>();
    }

    public Regles(int reveil, int survie, int mort) {
        this.reveil = reveil;
        this.survie = survie;
        this.mort = mort;

        this.useSuperCells = false;

        this.changeCharge = new HashMap<>();
    }

    public Regles(int reveil, int survie, int mort, int nbGenEvolution) {

        this.reveil = reveil;
        this.survie = survie;
        this.mort = mort;

        this.useSuperCells = true;
        this.nbGenSuperCells = nbGenEvolution;

        this.changeCharge = new HashMap<>();
    }

    public int getReveil() {
        return reveil;
    }

    public int getSurvie() {
        return survie;
    }

    public int getMort() {
        return mort;
    }

    public boolean useSuperCells() {
        return useSuperCells;
    }

    public boolean usePoseNeg() {
        return usePoseNeg;
    }

    public void appliquerRegles(Monde world) {
        for (int i = 0; i < world.size; i++) {
            for (int j = 0; j < world.size; j++) {
                int alive = 0;
                int charge = 0;

                for (int x = -1; x < 2; x++) {
                    for (int y = - 1; y < 2; y++) {
                        if ((i + x >= 0 && j + y >= 0) && (i + x < world.size && j + y < world.size)) {
                            if (!(x == 0 && y == 0)) {
                                if (world.grille[i + x][j + y].isAlive()) {
                                    alive++;
                                    charge += world.grille[i + x][j + y].getChargeValue();
                                }
                            }
                        }
                    }
                }

                //System.out.println("charge " + i + "," + j + " : " + charge);

                if (!usePoseNeg) {
                    if ((!world.getCellule(i, j).isAlive() && alive == reveil) || (world.getCellule(i, j).isAlive() && (alive < survie || alive > mort) && !world.getCellule(i, j).isImmortal())) {
                        world.changement.add(new Coordonnee(i, j));
                        world.getCellule(i, j).resetDureeVie();
                    } else if (world.getCellule(i, j).isAlive()) {
                        world.getCellule(i, j).incrementDureeVie();
                        if (useSuperCells && world.getCellule(i, j).getNbGenSurvie() >= 10) {
                            world.getCellule(i, j).setImmortal(true);
                        }
                    }
                } else {
                    if (charge > 1) {
                        changeCharge.put(new Coordonnee(i, j), 1);
                    } else if (charge < -1) {
                        changeCharge.put(new Coordonnee(i, j), -1);
                    } else {
                        changeCharge.put(new Coordonnee(i, j), 0);
                    }
                }
            }
        }

        for (Map.Entry<Coordonnee, Integer> entry : changeCharge.entrySet()) {
            //System.out.println("Iteration sur map");
            //System.out.println(world.getCellule(entry.getKey().getX(), entry.getKey().getY()).getChargeValue());
            world.getCellule(entry.getKey().getX(), entry.getKey().getY()).setChargeValue(entry.getValue());
        }
    }

    public void appliquerRegles(int minx, int maxx, int miny, int maxy, Monde world) {
        int alive;

        for (int i = minx; i < maxx; i++) {
            for (int j = miny; j < maxy; j++) {
                alive = 0;
                int charge = 0;

                for (int x = -1; x < 2; x++) {
                    for (int y = - 1; y < 2; y++) {
                        if ((i + x >= 0 && j + y >= 0) && (i + x < world.size && j + y < world.size)) {
                            if (!(x == 0 && y == 0)) {
                                if (world.grille[i + x][j + y].isAlive()) {
                                    alive++;
                                    charge += world.grille[i + x][j + y].getChargeValue();
                                }
                            }
                        }
                    }
                }


                //System.out.println("charge " + i + "," + j + " : " + charge);

                if (!usePoseNeg) {
                    synchronized (world.changement) {
                        if ((!world.getCellule(i, j).isAlive() && alive == reveil) || (world.getCellule(i, j).isAlive() && (alive < survie || alive > mort) && !world.getCellule(i, j).isImmortal())) {
                            world.changement.add(new Coordonnee(i, j));
                            world.getCellule(i, j).resetDureeVie();
                        } else if (world.getCellule(i, j).isAlive()) {
                            world.getCellule(i, j).incrementDureeVie();
                            if (useSuperCells && world.getCellule(i, j).getNbGenSurvie() >= 10) {
                                world.getCellule(i, j).setImmortal(true);
                            }
                        }
                    }
                } else {
                    if (charge > 1) {
                        changeCharge.put(new Coordonnee(i, j), 1);
                    } else if (charge < -1) {
                        changeCharge.put(new Coordonnee(i, j), -1);
                    } else {
                        changeCharge.put(new Coordonnee(i, j), 0);
                    }
                }
            }
        }

        for (Map.Entry<Coordonnee, Integer> entry : changeCharge.entrySet()) {
            world.getCellule(entry.getKey().getX(), entry.getKey().getY()).setChargeValue(entry.getValue());
        }
    }

    public void modifRegles(boolean useSuperCells, boolean usePosNeg) {
        this.usePoseNeg = usePosNeg;
        this.useSuperCells = useSuperCells;

    }
}
