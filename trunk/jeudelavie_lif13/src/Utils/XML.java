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

    public static GrilleImport importPreconstruit(String figure) throws ParserConfigurationException, SAXException, IOException {

        GrilleImport tmpExport = null;
        Cellule[][] tmp = null;
        
        System.out.println("Début d'import");

        File xmlFile = new File("src/data/preconstruit.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("motifs");
        
        System.out.println("Node Motifs récup");

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            node = node.getFirstChild();

            while (node != null && !(node.getNodeName().equalsIgnoreCase(figure))) {
                node = node.getNextSibling();
            }
            
            if (node != null) {
                System.out.println(node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element node_elt = (Element) node;

                    int sizex = new Integer(node_elt.getAttribute("taillex"));
                    int sizey = new Integer(node_elt.getAttribute("tailley"));

                    tmpExport = new GrilleImport(sizex, sizey);

                    tmp = new Cellule[sizex][sizey];

                    for (int m = 0; m < sizex; m++) {
                        for (int n = 0; n < sizey; n++) {
                            tmp[m][n] = new Cellule(0);
                        }
                    }
                }

                Node coords = node.getFirstChild();

                while (coords != null) {
                    if (coords.getNodeType() == Node.ELEMENT_NODE) {
                        Element coords_elt = (Element) coords;

                        int x = new Integer(coords_elt.getAttribute("x"));
                        int y = new Integer(coords_elt.getAttribute("y"));
                        String coords_value = coords.getTextContent().trim();

                        tmp[x][y].setAlive("alive".equalsIgnoreCase(coords_value));
                    }

                    coords = coords.getNextSibling();

                }
            }

        }
        
        System.out.println("tmpexport, sizex : " + tmpExport.getSizeX());
        
        tmpExport.setGrille(tmp);
        return tmpExport;

    }

    public static ArrayList<String> importListMotif() throws ParserConfigurationException, SAXException, IOException {
        ArrayList<String> list = new ArrayList<>();

        File xmlFile = new File("src/data/preconstruit.xml");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("list");

        for (int i = 0; i < nList.getLength(); i++) {

            Node node = nList.item(i).getFirstChild();

            while (node != null) {
                if (node.getNodeName().equals("figure")) {
                    list.add(node.getTextContent());
                }

                node = node.getNextSibling();
            }

        }

        return list;

    }

}
