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
    protected int team;

    public Cellule(int team) {
        this.underMotif = false;
        this.alive = false;
        this.team = team;
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
    
}
