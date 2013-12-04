/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


//a virer

package vue;

/**
 *
 * @author p1006149
 */
public class Grille{
    protected Case grille[][];
    protected int sizeX;
    protected int sizeY;
    
    public Grille(int x, int y)
    {
        this.sizeX = x;
        this.sizeY = y;
        
        grille = new Case[x][y];
        for (int i = 0; i<x; i++)
        {
            for (int j =0; j<y; j++)
            {
                grille[i][j] = new Case();
            }
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public Case[][] getGrille() {
        return grille;
    }
    
}
