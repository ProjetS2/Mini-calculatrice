/* 
 * Console.java                            14 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet de base d'une console
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public abstract class Console {

    /** Regex d'un int */
    public static final String REGEX_ENTIER =
            "(\\-{0,1})([0-9]*)(\\.[0]*){0,1}";
    
    /** Regex d'un double */
    public static final String REGEX_DOUBLE = 
            "(\\-{0,1})([0-9]{1,13})(\\.[0-9]+)?";

    /** Regex d'un op�rateur */
    public static final String REGEX_OPERATEUR = "[+*/-]{1}";
    
    /** Regex repr�sentant une zone m�moire */
    public static final String REGEX_ZONE_MEMOIRE = "[A-Z]{1}";
    
    /** Regex repr�sentant une plage m�moire */
    public static final String REGEX_PLAGE_MEMOIRES = "[A-Z]{1}\\.\\.[A-Z]{1}";
    
    /** Regex repr�sentant les coordonn�es d'une cellule sans blocage */
    public static final String REGEX_CELLULE = 
            "[A-Z]{1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex repr�sentant les coordonn�es d'une cellule avec blocage ($) */
    public static final String REGEX_CELLULE_BLOCAGE = 
            "[$]{0,1}[A-Z]{1}[$]{0,1}(([1-9])|(1[0-9]{1})|20)";
    
    /** Regex repr�sentant une plage de cellules */
    public static final String REGEX_PLAGE_CELLULES = 
            REGEX_CELLULE +  "\\.\\." + REGEX_CELLULE;
    
    /** Constante d'erreur de syntaxe */
    public static final int ERREUR_SYNTAXE = 1;
    
    /** Constante d'erreur de format */
    public static final int ERREUR_FORMAT = 2;
    
    /** Constante d'erreur si utilisation d'une variable non intialis�e */
    public static final int ERREUR_INTIALISATION = 3;
    
    /** Constante d'erreur si le nombre d'arguments n'est pas celui attendu */
    public static final int ERREUR_NB_ARGUMENTS = 4;
    
    /** Constante d'erreur de format de plage m�moire */
    public static final int ERREUR_PLAGE_MEMOIRES = 5;
    
    /** Constante d'erreur d'ordre de plage m�moire */
    public static final int ERREUR_ORDRE_PLAGE_MEMOIRES = 6;
    
    /** Constante d'erreur si un argument n'est pas une valeur */
    public static final int ERREUR_VALEUR_ARGUMENT = 7;
    
    /** Constante d'erreur si une affectation est attendue */
    public static final int ERREUR_AFFECTATION = 8;
    
    /** Objet permettant de d�finir le format des d�cimaux */
    private final DecimalFormat df = new DecimalFormat(); 
    
    /** R�f�rence permettant d'acc�der � la console et aux zones m�moire */
    private FenetrePrincipale laFenetre;
          
    /** Commande saisie par l'utilisateur */
    private String commande;
    
    /** Passe � true si une erreur a �t� trouv�e */
    private boolean erreurTrouvee;
    
    /** Saisie de l'utilisateur divis� dans un tableau � chaque espace */
    private String[] instructions;
    
    /** Message � retourner sur la console */
    private String aRetourner;
    
    /** Indice de l'argument o� il y a un probl�me */
    private int lieuMauvaisArgument;
    
    /**
     * Traite la commande saisie par l'utilisateur
     * @param commande Commande � traiter
     * @return r�sultat de la commande si elle est bonne, message d'erreur sinon
     */
    public abstract String traitementCommande(String commande);
    
    /**
     * R�initialise l'�tat de l'objet � chaque d�but d'instructions
     */
    public abstract void reinitialisation();
    
    /**
     * Recherche le lieu de l'erreur et affiche un message en fonction de cette
     * erreur
     * @param typeErreur Type d'erreur recherch�
     */
    public abstract void rechercheErreur(int typeErreur);
    
    /**
     * Teste si une chaine de caract�re est une zone m�moire ou une cellule
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'une zone m�moire ou d'une cellule, false sinon
     */
    public abstract boolean estUneMemoire(String aTester); 
                    
    /**
     * Acceseur � laFenetre
     * @return laFenetre 
     */
    public FenetrePrincipale getLaFenetre() {
        return laFenetre;
    }
    
    

    /**
     * Mutateur de laFenetre
     * @param laFenetre nouveau laFenetre
     */
    public void setLaFenetre(FenetrePrincipale laFenetre) {
        this.laFenetre = laFenetre;
    }

    /**
     * Acceseur � commande
     * @return commande 
     */
    public String getCommande() {
        return commande;
    }

    /**
     * Mutateur de commande
     * @param commande nouveau commande
     */
    public void setCommande(String commande) {
        this.commande = commande;
    }
    
    /**
     * Acceseur � erreurTrouvee
     * @return erreurTrouvee 
     */
    public boolean isErreurTrouvee() {
        return erreurTrouvee;
    }

    /**
     * Mutateur de erreurTrouvee
     * @param erreurTrouvee nouveau erreurTrouvee
     */
    public void setErreurTrouvee(boolean erreurTrouvee) {
        this.erreurTrouvee = erreurTrouvee;
    }

    /**
     * Acceseur � instructions
     * @return instructions 
     */
    public String[] getInstructions() {
        return instructions;
    }

    /**
     * Mutateur de instructions
     * @param instructions nouveau instructions
     */
    public void setInstructions(String[] instructions) {
        this.instructions = instructions;
    }

    /**
     * Acceseur � aRetourner
     * @return aRetourner 
     */
    public String getaRetourner() {
        return aRetourner;
    }

    /**
     * Mutateur de aRetourner
     * @param aRetourner nouveau aRetourner
     */
    public void setaRetourner(String aRetourner) {
        this.aRetourner = aRetourner;
    }

    /**
     * Acceseur � lieuMauvaisArgument
     * @return lieuMauvaisArgument 
     */
    public int getLieuMauvaisArgument() {
        return lieuMauvaisArgument;
    }

    /**
     * Mutateur de lieuMauvaisArgument
     * @param lieuMauvaisArgument nouveau lieuMauvaisArgument
     */
    public void setLieuMauvaisArgument(int lieuMauvaisArgument) {
        this.lieuMauvaisArgument = lieuMauvaisArgument;
    }
    
    /**
     * Acceseur � df
     * @return df 
     */
    public DecimalFormat getDf() {
        return df;
    }

    /**
     * Teste si une zone m�moire que l'on souhaite exploit�e est initialis�e
     * @param aTester Chaine � tester qui doit �tre une zone m�moire
     * @return true si la zone m�moire est initialis�e, false sinon
     */
    public boolean estInitialisee(String aTester) {
    
        return !this.laFenetre.getLaMemoire()
                .getContenuZones()[aTester.charAt(0) - 65].getText().equals("");
    }

    /**
     * V�rifie si une chaine est un op�rateur
     * @param aTester Chaine � tester
     * @return true si aTester est un op�rateur, false sinon
     */
    public static boolean estUnOperateur(String aTester) {
        return Pattern.compile(REGEX_OPERATEUR).matcher(aTester).matches();        
    }

    /**
     * Teste si une chaine de caract�re est un entier
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'un entier, false sinon
     */
    public static boolean estUnEntier(String aTester) {
        return Pattern.compile(REGEX_ENTIER).matcher(aTester).matches();
    }

    /**
     * Teste si une chaine de caract�re est un double
     * @param aTester Chaine � tester
     * @return true s'il s'agit d'un double, false sinon
     */
    public static boolean estUnDouble(String aTester) {
        return Pattern.compile(REGEX_DOUBLE).matcher(aTester).matches();
    }
    
}