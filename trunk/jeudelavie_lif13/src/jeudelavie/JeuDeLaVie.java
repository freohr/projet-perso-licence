/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jeudelavie;

import Controleur.Controle;
import Utils.XML;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import modele.Cellule;
import modele.Monde;
import org.xml.sax.SAXException;
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
        
        final Monde world = new Monde(40, 3, 2, 3);

        Controle controle = new Controle();

        controle.setModele(world);

        Vue fenetre;
        fenetre = new Vue(world.getSize(), controle);

        world.addObserver(fenetre);
        //fenetre.setControler(controle);

        fenetre.setVisible(true);//On rend la fenetre visible
        world.run();

    }
}
