/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import modele.Cellule;

/**
 *
 * @author p1006099
 */
public class XML {

    public static Cellule[][] importPreconstruit(String figure) throws ParserConfigurationException, SAXException, IOException {

        Cellule[][] tmp = null;

        File xmlFile = new File("data/preconstruit.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("preconstruit");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node.getNodeName().equals("motifs")) {
                
                node = node.getFirstChild();
                
                while(node != null) {

                if (node.getNodeName().equals(figure) && node.getNodeType() == Node.ELEMENT_NODE) {
                    // Récuperation de la figure à importer
                    Element elt = (Element) node;
                    int x = new Integer(elt.getAttribute("taillex"));
                    int y = new Integer(elt.getAttribute("tailley"));

                    // Initialisation de tmp à la taille de la figure à importer
                    tmp = new Cellule[x][y];

                    for (int m = 0; m < x; m++) {
                        for (int n = 0; n < y; n++) {
                            tmp[m][n] = new Cellule(0);
                        }
                    }

                    //Remplissage de tmp avec les infos importées
                    Node child = node.getFirstChild();
                    if (child != null) {
                        Element eltChild = (Element) child;
                        int casex = new Integer(eltChild.getAttribute("x"));
                        int casey = new Integer(eltChild.getAttribute("y"));

                        String state = eltChild.getNodeValue();
                        tmp[casex][casey].setAlive(state.equals("alive"));

                        child = child.getNextSibling();

                        while (child != null) {
                            eltChild = (Element) child;
                            casex = new Integer(eltChild.getAttribute("x"));
                            casey = new Integer(eltChild.getAttribute("y"));

                            state = eltChild.getNodeValue();
                            tmp[casex][casey].setAlive(state.equals("alive"));

                            child = child.getNextSibling();
                        }

                    }
                    break;
                }
                
                node = node.getNextSibling();
                }
            }

        }
        return tmp;

    }

    public static ArrayList<String> importListMotif() throws ParserConfigurationException, SAXException, IOException {
        ArrayList<String> list = new ArrayList<>();

        File xmlFile = new File("data/preconstruit.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("preconstruit");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeName().equals("list") && node.getNodeType() == Node.ELEMENT_NODE) {

                Node child = node.getFirstChild();

                if (child != null) {
                    Element eltChild = (Element) child;

                    list.add(eltChild.getNodeValue());

                    child = child.getNextSibling();
                    while (child != null) {
                        eltChild = (Element) child;

                        list.add(eltChild.getNodeValue());

                        child = child.getNextSibling();
                    }
                }
            }

        }

        return list;

    }

}
