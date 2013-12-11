/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;

/**
 *
 * @author p1006099
 */
public class Cellule {

    protected boolean alive;
    protected boolean underMotif;
    protected int x;
    protected int y;

    //Données utilisés par les reglèes personnalisées
    protected int team;
    protected int chargeValue;
    protected int nbGenSurvie;
    protected boolean immortal;

    public Cellule() {
        this.underMotif = false;
        this.alive = false;
        this.team = 0;
        this.chargeValue = 0;
        this.nbGenSurvie = 0;
        this.immortal = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getTeam() {
        return team;
    }

    public boolean isUnderMotif() {
        return underMotif;
    }

    public void setUnderMotif(boolean underMotif) {
        this.underMotif = underMotif;
    }

    public int getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(int chargeValue) {
        this.chargeValue = chargeValue;
    }

    public int getNbGenSurvie() {
        return nbGenSurvie;
    }

    public void setNbGenSurvie(int nbGenSurvie) {
        this.nbGenSurvie = nbGenSurvie;
    }

    public boolean isImmortal() {
        return immortal;
    }

    public void setImmortal(boolean immortal) {
        this.immortal = immortal;
    }
    
    public void incrementDureeVie() {
        nbGenSurvie++;
    }

    public void resetDureeVie() {
        nbGenSurvie = 0;
    }

}
