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
    
}
