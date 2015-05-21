/* 
 * FenetrePrincipale.java                            9 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Fen�tre principale de l'application
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
@SuppressWarnings("serial")
public class FenetrePrincipale extends JFrame {
    
    /** Onglets des zones m�moires et du tableur */
    private JTabbedPane lesOnglets;
    
    /** Barre de menu de l'application */
    private BarreDeMenu leMenu;
    
    /** Panneau de l'onglet zone m�moire */
    private PanneauZones laMemoire;
    
    /** Panneau de l'onglet du tableur */
    private PanneauTableur leTableur;
    
    /** Panneau du retour console */
    private PanneauCommande laConsole;
    
    /** Panneau permettant de rajouter un scroll � la console */
    private JScrollPane scrollPane;
    
    /** Zone de texte correpondant au retour console */
    private JTextArea retourConsole;

    /**
     * Constructeur de la fen�tre
     */
    public FenetrePrincipale() {
        
        // Propri�t�s de la fen�tre
        this.setTitle("Mini-Calcultrice");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("icone tableur.png").getImage());
        this.setSize(1024, 768);
        this.setLayout(null);
        this.getContentPane().setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        // Cr�ation du menu principal
        this.leMenu = new BarreDeMenu(this);
        this.setJMenuBar(this.leMenu);
               
        // Initialisation des onglets
        this.lesOnglets = new JTabbedPane(JTabbedPane.TOP); // onglets en hauts
        this.lesOnglets.setBounds(20, 10, 450, 580); // position et taille
        
        // On cr�� la console
        this.creationConsole();
        
        // Construction des panneaux des onglets...
        this.laMemoire = new PanneauZones();
        
        // Disposition et taille du panneau de la m�moire
        this.laMemoire.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        this.leTableur = new PanneauTableur();
        
        // Disposition et taille du panneau du tableur
        this.leTableur.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        // ... qu'on ajoute ensuite
        this.lesOnglets.addTab("Zones m�moires", this.laMemoire);
        this.lesOnglets.addTab("Tableur", this.leTableur);
        
        // On d�sactive le tableur au lancement
        this.lesOnglets.setEnabledAt(1, false);
        this.lesOnglets.setSelectedIndex(0); 
        
        // Panneau console
        this.laConsole = new PanneauCommande(this);
        this.add(this.laConsole);
        
        this.getContentPane().add(this.lesOnglets);
               
        // On positionne le curseur sur la ligne de commande
        this.laConsole.getLigneDeCommande().requestFocus();
        
        this.getContentPane().repaint();    
        this.validate();
    }

    /**
     * Construit la console d'affichage
     */
    private void creationConsole() {
        
        this.retourConsole = new JTextArea(new String(), 10, 10);

        this.scrollPane = new JScrollPane(this.retourConsole);        
        this.scrollPane.setBounds(485, 30, 510, 560);    // Position et taille
        this.retourConsole.setEditable(false);           // D�sactive l'�criture
        this.retourConsole.setBackground(Color.BLACK);   // Arri�re plan
        this.retourConsole.setForeground(Color.GREEN);   // Couleur police
        this.retourConsole.setFont(new Font("Courier", 0, 12)); // Police

        this.retourConsole.setLineWrap(true);   // Empeche le scroll horizontal
        this.retourConsole.setWrapStyleWord(true); // sans d�couper les mots
        
        this.retourConsole.append("Mini-calculatrice [version 0.4]"
                + "\nIUT INFO1 2014-2015\n\n");
        
        this.getContentPane().add(this.scrollPane);
    }
    
    /**
     * Ajoute la chaine de caract�re pass�e en argument � la suite de la console
     * et envoie le scroll au bas de la console
     * @param aAjouter Chaine � ajouter
     */
    public void ajoutLigneConsole(String aAjouter) {
        // Ajoute le texte � la suite de la console
        this.retourConsole.append(aAjouter + "\n");

        // Envoie le scroll s'il est pr�sent au plus bas
        this.retourConsole.setCaretPosition(
                this.retourConsole.getDocument().getLength());        
    }
    
    /**
     * Acceseur � lesOnglets
     * @return lesOnglets 
     */
    public JTabbedPane getLesOnglets() {
        return lesOnglets;
    }

    /**
     * Accesseur � laConsole
     * @return laConsole 
     */
    public PanneauCommande getLaConsole() {
        return laConsole;
    }

    /**
     * Mutateur de laMemoire
     * @param laMemoire nouveau laMemoire
     */
    public void setLaMemoire(PanneauZones laMemoire) {
        this.laMemoire = laMemoire;
    }

    /**
     * Acceseur � laMemoire
     * @return laMemoire 
     */
    public PanneauZones getLaMemoire() {
        return laMemoire;
    }

    /**
     * Acceseur � leTableur
     * @return leTableur 
     */
    public PanneauTableur getLeTableur() {
        return leTableur;
    }

    /**
     * Acceseur � leMenu
     * @return leMenu 
     */
    public BarreDeMenu getLeMenu() {
        return leMenu;
    }  
}