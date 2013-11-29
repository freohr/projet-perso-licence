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
    Case grille[][];
    
    public Grille(int x, int y)
    {
        grille = new Case[x][y];
        for (int i = 0; i<x; i++)
        {
            for (int j =0; j<y; j++)
            {
                grille[i][j] = new Case();
            }
        }
    }
}
