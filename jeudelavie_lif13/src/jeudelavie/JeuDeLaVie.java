/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeudelavie;

import javax.swing.SwingUtilities;
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

        final Monde test = new Monde(15, 3, 2, 3);
        Vue fenetre = new Vue(test.getSize());
        fenetre.setVisible(true);//On rend la fenetre visible

        test.addObserver(fenetre);
        
        test.run();

    }
}
