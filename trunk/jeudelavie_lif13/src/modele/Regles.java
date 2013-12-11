/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

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

    public Regles() {
        this.useSuperCells = false;

        this.reveil = 3;
        this.survie = 2;
        this.mort = 3;
    }

    public Regles(int reveil, int survie, int mort) {
        this.reveil = reveil;
        this.survie = survie;
        this.mort = mort;

        this.useSuperCells = false;
    }

    public Regles(int reveil, int survie, int mort, int nbGenEvolution) {

        this.reveil = reveil;
        this.survie = survie;
        this.mort = mort;

        this.useSuperCells = true;
        this.nbGenSuperCells = nbGenEvolution;
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
                if (!usePoseNeg) {
                    if ((!world.getCellule(i, j).isAlive() && alive == reveil) || (world.getCellule(i, j).isAlive() && (alive < survie || alive > mort) && !world.getCellule(i, j).isImmortal())) {
                        world.changement.add(new Coordonnee(i, j));
                        world.getCellule(i, j).resetDureeVie();
                    } else if (world.getCellule(i, j).isAlive()) {
                        world.getCellule(i, j).incrementDureeVie();
                        if (useSuperCells && world.getCellule(i, j).getNbGenSurvie() >= 5) {
                            world.getCellule(i, j).setImmortal(true);
                        }
                    }
                } else {
                    if (charge <= -3) {
                        world.getCellule(i, j).setChargeValue(-1);
                    } else if (charge >= 3) {
                        world.getCellule(i, j).setChargeValue(1);
                    } else {
                        world.getCellule(i, j).setChargeValue(0);
                    }
                }
            }
        }
    }

    public void modifRegles(boolean useSuperCells, boolean usePosNeg) {
        this.usePoseNeg = usePosNeg;
        this.useSuperCells = useSuperCells;

    }

}
