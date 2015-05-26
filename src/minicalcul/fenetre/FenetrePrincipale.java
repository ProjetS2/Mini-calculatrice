/* 
 * FenetrePrincipale.java                            14 avr. 2015
 * IUT INFO1 Projet S2 2014-2015
 */
package minicalcul.fenetre;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import minicalcul.programme.commandes.Commandes;

/**
 * Fenêtre principale de l'application contenant tous les objets graphiques
 * principaux.
 * @author Thomas Affre
 * @author Thibaut Méjane
 * @author Florian Louargant
 * @author Clément Zeghmati
 * @version 1.1
 */
@SuppressWarnings("serial")
public class FenetrePrincipale extends JFrame {
    
    /** Onglets des zones mémoires et du tableur */
    private JTabbedPane lesOnglets;
    
    /** Barre de menu de l'application */
    private BarreDeMenu leMenu;
    
    /** Panneau de l'onglet zone mémoire */
    private PanneauZones laMemoire;
    
    /** Panneau de l'onglet du tableur */
    private PanneauTableur leTableur;
    
    /** Panneau du retour console */
    private PanneauCommande laConsole;
    
    /** Panneau permettant de rajouter un scroll à la console */
    private JScrollPane scrollPane;
    
    /** Zone de texte correpondant au retour console */
    private JTextArea retourConsole;

    /**
     * Constructeur de la fenêtre
     */
    public FenetrePrincipale() {
        
        // Propriétés de la fenêtre
        this.setTitle("Mini-Calculatrice");      // Titre
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setIconImage(new ImageIcon("icone.png").getImage()); // Logo
        this.setSize(1024, 768);                // Taille
        this.getContentPane().setLayout(null);  // Disposition par coordonnées
        this.setResizable(false);               // Non redimensionnable
        this.setLocationRelativeTo(null);       // Centrer à l'ouverture
        this.setVisible(true);                  // Affichage de la fenêtre
        
        // Message de confirmation lorsque l'utilisateur ferme la fenetre
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                fermetureApplication();
            }
        });
        
        // Création du menu principal
        this.leMenu = new BarreDeMenu(this);
        this.setJMenuBar(this.leMenu);
               
        // Initialisation des onglets
        this.lesOnglets = new JTabbedPane(JTabbedPane.TOP); // onglets en hauts
        this.lesOnglets.setBounds(20, 10, 450, 580);       // position et taille
        
        // On créé la console
        this.creationConsole();
        
        // Construction des panneaux des onglets...
        this.laMemoire = new PanneauZones();
        
        // Disposition et taille du panneau de la mémoire
        this.laMemoire.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        // Création du tableur
        this.leTableur = new PanneauTableur();
        
        // Disposition et taille du panneau du tableur
        this.leTableur.setBounds(0, 0, this.getLesOnglets().getWidth(),
                this.getLesOnglets().getHeight());
        
        // ... qu'on ajoute ensuite
        this.lesOnglets.addTab("Zones mémoires", this.laMemoire);
        this.lesOnglets.addTab("Tableur", this.leTableur);
        
        // On désactive le tableur au lancement
        this.lesOnglets.setEnabledAt(1, false);
        this.lesOnglets.setSelectedIndex(0); 
        
        // Panneau console
        this.laConsole = new PanneauCommande(this);
        this.add(this.laConsole);
        
        // Ajout des onglets au panneau de la fenêtre
        this.getContentPane().add(this.lesOnglets);
               
        // On positionne le curseur sur la ligne de commande
        this.laConsole.getLigneDeCommande().requestFocus();
        
        // On valide tous les ajouts
        this.getContentPane().repaint();    
        this.validate();
    }
    
    /**
     * Construit la console qui affichera un historique des opérations effectués
     * avec l'incidence qu'elles auront eu sur l'application (ex : résultat
     * opération, affectation, erreur...)
     */
    private void creationConsole() {
        
        // Construction
        this.retourConsole = new JTextArea(new String(), 10, 10);

        // Propriétés du scroll
        this.scrollPane = new JScrollPane(this.retourConsole);        
        this.scrollPane.setBounds(485, 30, 510, 560);    // Position et taille
        
        // Propriétés de la console
        this.retourConsole.setEditable(false);           // Désactive l'écriture
        this.retourConsole.setBackground(Color.BLACK);   // Arrière plan
        this.retourConsole.setForeground(Color.GREEN);   // Couleur police
        this.retourConsole.setFont(new Font("Courier", 0, 12)); // Police

        this.retourConsole.setLineWrap(true); // Empeche le scroll horizontal...
        this.retourConsole.setWrapStyleWord(true); // ...sans découper les mots
        
        // Premier texte affiché à l'ouverture de la calculatrice
        this.ajoutLigneConsole("Mini-calculatrice [version 1.1]"
                + "\nIUT INFO1 2014-2015\n");
        
        // Ajout au panneau de la fenêtre
        this.getContentPane().add(this.scrollPane);
    }
    
    /**
     * Ajoute la chaine de caractères passée en argument à la suite de la 
     * console et envoie le scroll au bas de la console
     * @param aAjouter Chaine à ajouter
     */
    public void ajoutLigneConsole(String aAjouter) {
        // Ajoute le texte à la suite de la console
        this.retourConsole.append(aAjouter + "\n");

        // Envoie le scroll, s'il est présent, au plus bas
        this.retourConsole.setCaretPosition(
                this.retourConsole.getDocument().getLength());        
    }
    
    /**
     * Appelée lorsque l'utilisateur souhaite fermer l'application. Un message
     * de confirmation lui sera affiché.
     */
    public void fermetureApplication() {
        if (JOptionPane.showConfirmDialog(this, "<html><center>"
                + "Souhaitez-vous vraiment quitter ?<br/>Les "
                + "données non sauvegardées seront définitivement perdus."
                + "</center</html>",
                "Quitter", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Accesseur à lesOnglets
     * @return lesOnglets 
     */
    public JTabbedPane getLesOnglets() {
        return lesOnglets;
    }

    /**
     * Accesseur à laConsole
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
     * Accesseur à laMemoire
     * @return laMemoire 
     */
    public PanneauZones getLaMemoire() {
        return laMemoire;
    }

    /**
     * Accesseur à leTableur
     * @return leTableur 
     */
    public PanneauTableur getLeTableur() {
        return leTableur;
    }

    /**
     * Accesseur à leMenu
     * @return leMenu 
     */
    public BarreDeMenu getLeMenu() {
        return leMenu;
    }  
    
    /**
     * Lancement de l'application
     * @param args unused
     */
    public static void main(String[] args) {
        new Commandes(new FenetrePrincipale());
    }
}