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

    public Regles(int reveil, int survie, int mort) {
        this.reveil = reveil;
        this.survie = survie;
        this.mort = mort;
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
