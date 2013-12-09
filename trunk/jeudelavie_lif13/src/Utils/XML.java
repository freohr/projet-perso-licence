/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.File;
import java.io.IOException;
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
    
    public static Cellule[][] importPreconstruit(String path, String figure) throws ParserConfigurationException, SAXException, IOException{
        
        File xmlFile = new File(path);
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("preconstruit");
        
        
        
        return null;
        
    }
    
}
