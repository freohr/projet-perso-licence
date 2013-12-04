/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeudelavie;

import modele.Monde;
import vue.Vue;

/**
 *
 * @author p1006149
 */
public class JeuDeLaVie {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final Monde test = new Monde(40, 3, 2, 3);
        
        
        Vue fenetre;
        fenetre = new Vue(test.getSize(), test);
        fenetre.getMonde().addObserver(fenetre);
        fenetre.setVisible(true);//On rend la fenetre visible
        
    }
}
