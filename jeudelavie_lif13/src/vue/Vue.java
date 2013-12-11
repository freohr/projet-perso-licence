/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import java.awt.BorderLayout;
import Controleur.Controle;
import Utils.CellStates;
import Utils.XML;
import static Utils.XML.importListMotif;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import modele.Monde;
import org.xml.sax.SAXException;

/**
 *
 * @author p1006149
 */
public class Vue extends JFrame implements Observer {

    protected Grille g;
    //Pour les eventListeners
    protected JPanel panelPrincipal;
    protected JPanel panelGrid;
    protected JLabel labelTaux;
    protected JButton boutonPause;
    protected JComboBox boxImport;
    protected AffichageRegles panneauRegles;
    protected Controle controle;

    public Vue(int size) {
        super();

        g = new Grille(size, size);
        buildWindow(size);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

    }

    public Vue(int size, Controle controle1) {
        super();

        g = new Grille(size, size, controle1);
        buildWindow(size);

        controle = controle1;
        panneauRegles = new AffichageRegles(controle);
        panneauRegles.setVisible(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

    }

    public void setControler(Controle controle1) {
        this.controle = controle1;
    }

    private void buildWindow(int size) {

        setTitle("Le jeu de la vie");
        setSize(800, 800);
        buildMenu();
        JPanel panel = new JPanel(new BorderLayout());

        // Interface
        panel.add(buildInterface(size), BorderLayout.PAGE_START);

        // Grille
        panel.add(buildPanelGrid(size), BorderLayout.CENTER);

        //panel.add(buildButtons());
        panelPrincipal = panel;
        add(panelPrincipal);
    }

    private JPanel buildInterface(int size) {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        JPanel panel = new JPanel(layout);
        panel.setName("interface");
        System.out.println("panel_name : " + panel.getName());

        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.3;
        c.weighty = 0.3;

        // Colonne 1
        JLabel labelSize = new JLabel("Taille de la grille");
        labelSize.setHorizontalAlignment(JLabel.CENTER);
        labelSize.setName("labelSize");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelSize, c);

        JPanel panelTaux = new JPanel(layout);
        panelTaux.setName("panelTaux");

        JLabel labelTauxInit = new JLabel("Taux d'initialisation");
        labelTauxInit.setName("labelTaux");
        labelTauxInit.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelTauxInit, c);

        JLabel labelInfoTaux = new JLabel("(% de cellules vivantes)");
        labelInfoTaux.setName("labelInfoTaux");
        labelInfoTaux.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelInfoTaux, c);

        JLabel labelPercentTaux = new JLabel("50%");
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.CENTER;
        this.labelTaux = labelPercentTaux;
        panelTaux.add(labelPercentTaux, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        panel.add(panelTaux, c);

        c.gridheight = 1;

        // Déplacer sliders ici (lignes 2,3)
        // Slider de vitesse de génération(ligne 4)
        JLabel labelVitesseGeneration = new JLabel("Vitesse de génération (rapide <-> lent)");
        labelVitesseGeneration.setName("labelVitesse");
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelVitesseGeneration, c);

        JSlider sliderVitesse = new JSlider(0, 2000, 200);
        sliderVitesse.setName("sliderVitesse");
        sliderVitesse.addChangeListener(new SpeedSliderListener());
        sliderVitesse.setMajorTickSpacing(500);
        sliderVitesse.setMinorTickSpacing(100);
        sliderVitesse.setLabelTable(sliderVitesse.createStandardLabels(500));
        sliderVitesse.setPaintLabels(true);
        sliderVitesse.setPaintTicks(true);
        sliderVitesse.setSnapToTicks(true);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderVitesse, c);

        //Slider de vitesse de calcul(ligne5)
        JLabel labelVitesseCalcul = new JLabel("Vitesse de calcul (nb de threads)");
        labelVitesseCalcul.setName("labelVitesseCalcul");
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelVitesseCalcul, c);

        JSlider sliderVitesseCalcul = new JSlider(0, 20, 1);
        sliderVitesseCalcul.setName("sliderVitesseCalcul");
        sliderVitesseCalcul.addChangeListener(new NbThreadsListener());
        sliderVitesseCalcul.setMajorTickSpacing(5);
        sliderVitesseCalcul.setMinorTickSpacing(1);
        sliderVitesseCalcul.setLabelTable(sliderVitesseCalcul.createStandardLabels(5));
        sliderVitesseCalcul.setPaintLabels(true);
        sliderVitesseCalcul.setPaintTicks(true);
        sliderVitesseCalcul.setSnapToTicks(true);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderVitesseCalcul, c);

        JLabel labelForme = new JLabel("Import de motifs préconstruits");
        labelForme.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelForme, c);

        //Colonne 2
        JTextField textFieldTaille = new JTextField(new String() + size);
        textFieldTaille.setName("textFieldTaille");
        textFieldTaille.addActionListener(new InitTextFieldListener());
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textFieldTaille, c);

        JSlider sliderTaux = new JSlider(1, 100, 50);
        sliderTaux.setName("sliderTaux");
        sliderTaux.addChangeListener(new TauxSliderListener());
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderTaux, c);

        ArrayList<String> listeMotifs = new ArrayList<>();
        try {
            listeMotifs = importListMotif();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(Vue.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Erreur de récupération de la liste des motifs depuis XML.");
        }
        JComboBox<Object> listMotifs = new JComboBox<>(listeMotifs.toArray());
        listMotifs.setName("listMotifs");
        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        boxImport = listMotifs;
        panel.add(listMotifs, c);

        // Colonne 3
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JButton boutonInit = new JButton("Initialiser");
        boutonInit.setName("boutonName");
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 10, 5, 10);
        boutonInit.addActionListener(new InitListener());
        panel.add(boutonInit, c);

        JButton pauseButton = new JButton("Lancer");
        pauseButton.setName("boutonPause");
        pauseButton.addActionListener(new PauseListener());
        c.gridx = 2;
        c.gridy = 2;
        c.gridheight = 1;
        this.boutonPause = pauseButton;
        panel.add(pauseButton, c);

        JButton boutonReset = new JButton("Reset");
        boutonReset.setName("boutonReset");
        boutonReset.addActionListener(new ResetListener());
        c.gridx = 2;
        c.gridy = 3;
        panel.add(boutonReset, c);

        JButton boutonImport = new JButton("Importer");
        boutonImport.setName("boutonImport");
        boutonImport.addActionListener(new ImportListener());
        c.gridx = 2;
        c.gridy = 4;
        panel.add(boutonImport, c);

        return panel;
    }

    private void buildMenu() {
        JMenuBar jm = new JMenuBar();

        JMenu m = new JMenu("Jeu");

        JMenuItem save = new JMenuItem(new SaveListener());
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

        JMenuItem load = new JMenuItem(new LoadLIstener());
        load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        JMenuItem regle = new JMenuItem(new MenuReglesListener());
        regle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        //  menuItem.setAccelerator(KeyStroke.getKeyStroke(
        //  KeyEvent.VK_1, ActionEvent.ALT_MASK));

        m.add(regle);
        m.add(save);
        m.add(load);

        jm.add(m);

        setJMenuBar(jm);
    }

    private JPanel buildPanelGrid(int size) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setName("PanelGrid");
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;

        panel.add(buildGrid(size), c);
        panel.setName("cellGridPanel");
        panelGrid = panel;
        return panel;
    }

    private JPanel buildGrid(int size) {
        JPanel panel = new JPanel(new GridLayout(size, size));
        Border blackline = BorderFactory.createLineBorder(Color.black, 1);
        panel.setName("grid");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                panel.add(g.grille[i][j]);
                g.grille[i][j].setBorder(blackline);
            }
        }
        panel.setBorder(blackline);
        panel.setName("cellGrid");
        return panel;

    }

    public void updateGrille(Monde world) {
        // Si la grille du monde à afficher est différente de la grille d'affichage de la vue (en cas de nouveau monde par ex.)
        if (world.getSize() != g.getSizeX() || world.getSize() != g.getSizeY()) {

            Grille tmp = new Grille(world.getSize(), world.getSize(), controle);
            this.g = tmp;

            JComponent tmpGrid = buildGrid(world.getSize());
            tmpGrid.setName("cellGrid");

            GridBagConstraints c = new GridBagConstraints();

            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;

            System.out.println(panelGrid.getName());

            panelGrid.remove(panelGrid.getComponent(0));
            panelGrid.add(tmpGrid, c);

            SwingUtilities.updateComponentTreeUI(panelPrincipal);
        }

        //System.out.println("modif état");
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.hasMotif()) {
                    if (world.getCellule(i, j).isUnderMotif()) {
                        if (world.getMotifCase(i - world.getMotifOffsetX(), j - world.getMotifOffsetY()).isAlive()) {
                            g.grille[i][j].setCaseColor(CellStates.UNDER_MOTIF);
                        } else {
                            g.grille[i][j].setCaseColor(CellStates.DEAD);
                        }
                    } else if (world.getCellule(i, j).isAlive()) {
                        g.grille[i][j].setCaseColor(CellStates.ALIVE);
                    } else {
                        g.grille[i][j].setCaseColor(CellStates.DEAD);
                    }
                } else {
                    if (world.getRegle().usePoseNeg()) {
                        switch (world.getCellule(i, j).getChargeValue()) {
                            case -1:
                                if (world.getRegle().useSuperCells() && world.getCellule(i, j).isImmortal()) {
                                    g.grille[i][j].setCaseColor(CellStates.IMMORTAL_NEGATIVE);
                                } else {
                                    g.grille[i][j].setCaseColor(CellStates.NEGATIVE);
                                }
                                break;

                            case 1:
                                if (world.getRegle().useSuperCells() && world.getCellule(i, j).isImmortal()) {
                                    g.grille[i][j].setCaseColor(CellStates.IMMORTAL_POSITIVE);
                                } else {
                                    g.grille[i][j].setCaseColor(CellStates.POSITIVE);
                                }
                                break;

                            case 0:
                                g.grille[i][j].setCaseColor(CellStates.DEAD);
                                break;

                            default:
                                g.grille[i][j].setCaseColor(CellStates.DEAD);
                                break;

                        }
                    } else if (world.getCellule(i, j).isAlive()) {
                        if (world.getRegle().useSuperCells() && world.getCellule(i, j).isImmortal()) {
                            g.grille[i][j].setCaseColor(CellStates.IMMORTAL);
                        } else {
                            g.grille[i][j].setCaseColor(CellStates.ALIVE);
                        }
                    } else {
                        g.grille[i][j].setCaseColor(CellStates.DEAD);
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //System.out.println("update vue");
        updateGrille((Monde) o);

    }

    // Les event Listeners
    private class InitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("appui bouton");
            JComponent c = (JComponent) e.getSource();

            for (Component c2 : c.getParent().getComponents()) {
                if (c2 instanceof JTextField) {
                    JTextField jTextField = (JTextField) c2;

                    //System.out.println("textfield");
                    //System.out.println(jTextField.getText());
                    controle.initMonde(new Integer(jTextField.getText()));
                }
            }
        }
    }

    private class PauseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            controle.pause();
            if (((JButton) e.getSource()).getText().equals("Pause")) {
                ((JButton) e.getSource()).setText("Resume");
            } else {
                ((JButton) e.getSource()).setText("Pause");
            }
            ((JButton) e.getSource()).repaint();
        }
    }

    private class ResetListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boutonPause.setText("Lancer");

            controle.reset();
        }
    }

    private class SpeedSliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider js = (JSlider) e.getSource();

            controle.modifThreadSpeed(js.getValue());
        }
    }

    private class TauxSliderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider js = (JSlider) e.getSource();

            labelTaux.setText(js.getValue() + "%");
            controle.setTaux(js.getValue());
        }
    }

    private class NbThreadsListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider js = (JSlider) e.getSource();

            controle.setNbThreads(js.getValue());
        }
    }

    private class ImportListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            controle.importMotif((String) boxImport.getSelectedItem());
        }
    }

    private class MenuReglesListener extends AbstractAction {

        public MenuReglesListener() {
            super("Regles");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("appui menu");
            panneauRegles.setVisible(true);
        }
    }

    private class SaveListener extends AbstractAction {

        public SaveListener() {
            super("Sauvegarde");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String path = JOptionPane.showInputDialog(rootPane, "Veullez entrer un nom pour le fichier de sauvegarde\n(Dossier de sauvegarde : src/data/save)");
            if (path != null && path.length() > 0) {
                controle.save(path);
            }
        }
    }

    private class LoadLIstener extends AbstractAction {

        public LoadLIstener() {
            super("Chargement");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String path = JOptionPane.showInputDialog(rootPane, "Veuillez entrer le nom du fichier à charger\n(Dossier de sauvegarde src/data/save)");
            if (path != null && path.length() > 0) {
                try {
                    controle.loadGrille(XML.chargeGrille(path));
                } catch (ParserConfigurationException | SAXException ex) {
                    System.err.println("Erreur lors de l'importation du fichier " + path + ".xml");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(rootPane, "Le fichier demandé n'existe pas", "Fichier non existant", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    private class InitTextFieldListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField field = (JTextField) e.getSource();
            controle.initMonde(new Integer(field.getText()));
        }
    }
}
