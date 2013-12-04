/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;

import javax.swing.border.Border;
import modele.Coordonnee;
import modele.Monde;

/**
 *
 * @author p1006149
 */
public class Vue extends JFrame implements Observer {

    protected Grille g;
    protected JPanel panelPrincipal;
    protected final Monde monde;

    public Vue(int size, Monde monde) {
        super();

        panelPrincipal = new JPanel();
        g = new Grille(size, size);
        buildInterface(size);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

        this.monde = monde;
    }

    public Monde getMonde() {
        return monde;
    }

    private void buildInterface(int size) {

        setTitle("Le jeu de la vie");
        setSize(800, 800);
        buildMenu();

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        JComponent panel = new JPanel(layout);

        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 0.1;

        // Colonne 1
        JLabel labelSize = new JLabel("Taille de la grille");
        labelSize.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelSize, c);

        JPanel panelTaux = new JPanel(layout);

        JLabel labelTaux = new JLabel("Taux d'initialisation");
        labelTaux.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelTaux, c);

        JLabel labelPercent = new JLabel("(% de cellules vivantes)");
        labelPercent.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelPercent, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(panelTaux, c);

        JLabel labelForme = new JLabel("Forme des cellules");
        labelForme.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelForme, c);

        //Colonne 2
        JTextField textFieldTaille = new JTextField("0");
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textFieldTaille, c);

        JSlider sliderTaux = new JSlider(1, 100, 50);
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderTaux, c);

        String[] cells = {"Carré", "Hexagone", "Triangle"};
        JComboBox<String> listCells = new JComboBox<>(cells);
        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(listCells, c);

        // Colonne 3
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JButton boutonInit = new JButton("Initialiser");
        c.gridx = 2;
        c.gridy = 0;
        boutonInit.addActionListener(new InitListener());
        panel.add(boutonInit, c);

        JButton boutonPause = new JButton("Pause");
        c.gridx = 2;
        c.gridy = 1;
        panel.add(boutonPause, c);

        JButton boutonReset = new JButton("Reset");
        c.gridx = 2;
        c.gridy = 2;
        panel.add(boutonReset, c);

        // Slider de vitesse (ligne 4)
        JLabel labelVitesse = new JLabel("Vitesse de génération (rapide <-> lent)");
        c.gridx = 0;
        c.gridy = 3;
        panel.add(labelVitesse, c);

        JSlider sliderVitesse = new JSlider(10, 2000, 1005);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderVitesse, c);

        // Grille
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.NONE;

        JComponent grid = buildGrid(size);

        panel.add(grid, c);

        //panel.add(buildButtons());
        add(panel);
    }

    private void buildMenu() {
        JMenuBar jm = new JMenuBar();

        JMenu m = new JMenu("Jeu");

        JMenuItem mi = new JMenuItem("Partie");

        m.add(mi);

        jm.add(m);

        setJMenuBar(jm);
    }

    private JComponent buildGrid(int size) {

        JComponent panel = new JPanel(new GridLayout(size, size));
        Border blackline = BorderFactory.createLineBorder(Color.black, 1);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                panel.add(g.grille[i][j]);
                g.grille[i][j].setBorder(blackline);
            }
        }
        panel.setBorder(blackline);
        return panel;

    }

    private JComponent buildButtons() {
        JComponent panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton butt = new JButton("recommencer");
        panel.add(butt);
        JButton butt2 = new JButton("pause");
        panel.add(butt2);
        JButton butt3 = new JButton("continuer");
        panel.add(butt3);
        return panel;
    }

    public void updateGrille(Monde world) {
        System.out.println("Update grille");
        System.out.println("monde.size " + world.getSize());
        System.out.println("grille.size_x " + g.getSizeX());
        
        // Si la grille du monde à afficher est différente de la grille d'affichage de la vue (en cas de nouveau monde par ex.)
        if (world.getSize() != g.getSizeX() || world.getSize() != g.getSizeY()) {
            
            System.out.println("Taille différente");

            Grille tmp = new Grille(world.getSize(), world.getSize());
            this.g = tmp;

            for (Component c : this.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel jPanel = (JPanel) c;
                    if ("panel".equals(jPanel.getName())) {
                        for (Component c2 : jPanel.getComponents()) {
                            if (c2 instanceof JPanel) {
                                JPanel jPanel2 = (JPanel) c2;

                                if ("grid".equals(jPanel.getName())) {
                                    GridBagConstraints gbc = new GridBagConstraints();

                                    gbc.gridx = 0;
                                    gbc.gridy = 4;
                                    gbc.gridwidth = 3;
                                    gbc.gridheight = 1;
                                    gbc.weighty = 1;
                                    gbc.weightx = 1;
                                    gbc.fill = GridBagConstraints.NONE;

                                    JComponent tmpGrid = buildGrid(world.getSize());

                                    jPanel.remove(jPanel2);
                                    jPanel.add(tmpGrid, gbc);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("modif état");
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                // Utiliser l'arraylist changement de monde
                for (Coordonnee coord : world.getChangement()) {
                    if (world.getCellule(coord.getX(), coord.getY()).isAlive()) {
                        g.getGrille()[coord.getX()][coord.getY()].setCaseColor(Case.DEAD);
                    } else {
                        g.getGrille()[coord.getX()][coord.getY()].setCaseColor(Case.ALIVE);
                    }
                }
                /*if (monde.getCellule(i, j).isAlive()) {
                 g.grille[i][j].setCaseColor(0);
                 } else {
                 g.grille[i][j].setCaseColor(1);
                 }*/
            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("update vue");
        updateGrille((Monde) o);
    }

    // Les event Listeners
    private class InitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("appui bouton");
            JComponent c = (JComponent) e.getSource();

            for (Component c2 : c.getParent().getComponents()) {
                if (c2 instanceof JTextField) {
                    JTextField jTextField = (JTextField) c2;
                    
                    System.out.println("textfield");
                    System.out.println(jTextField.getText());

                    monde.init(new Integer(jTextField.getText()));
                }
            }
        }
    }
}
