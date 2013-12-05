/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



package vue;

import Controleur.Controle;
import java.awt.Color;

/**
 *
 * @author p1006149
 */
public class Grille{
    protected Case grille[][];
    protected int sizeX;
    protected int sizeY;
    
    public Grille(int x, int y, Controle controle)
    {
        this.sizeX = x;
        this.sizeY = y;
        
        grille = new Case[x][y];
        for (int i = 0; i<x; i++)
        {
            for (int j =0; j<y; j++)
            {
                grille[i][j] = new Case(i, j, controle);
            }
        }
        this.sizeX = x;
        this.sizeY = y;
    }
    
    public Grille(int x, int y)
    {
        this.sizeX = x;
        this.sizeY = y;
        
        grille = new Case[x][y];
        for (int i = 0; i<x; i++)
        {
            for (int j =0; j<y; j++)
            {
                grille[i][j] = new Case(i, j);
            }
        }
        this.sizeX = x;
        this.sizeY = y;
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
    
    public void afficher_term()
    {
        System.out.println("affichage de la grille :");
        for(int i = 0; i<this.sizeX; i++)
        {
            for (int j = 0; j<this.sizeY; j++)
            {
                if(this.grille[i][j].getCaseColor() == Color.white)
                    System.out.print("0");
                else
                    System.out.print("1");
                System.out.print(" ");
            }
            System.out.println("");
        }
    }
    
}
