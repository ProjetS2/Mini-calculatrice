/* 
 * ConsoleGestionTableur.java                            19 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;
import minicalcul.programme.tableur.Tableur;

/**
 * Objet de gestion des commandes du tableur
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public class ConsoleGestionTableur extends ConsoleGestionMemoire {
    
    /** Commandes disponibles pour le tableur */
    public static final String[] COMMANDES =
        {"CONT", "INIT", "FORM", "COPIER", "COPVAL", "RAZ", "AIDE"};

    /** R�f�rence au tableur afin de pouvoir int�ragir avec */
    private Tableur leTableur;
    
    /** 
     * R�f�rence au groupe de consoles afin de pouvoir s'en servir pour 
     * des calculs sur le tableur
     */
    private Commandes lesConsoles;
    
    /** 
     * Plage de cellules consern�es par l'op�ration envoy�e sous forme
     * d'indices (A1=[0,0]..Z20[19,25]) 
     */
    private int[][] plageCellules;
    
    /** Formule � affecter consern� par l'op�ration */
    private String formule;

    /**
     * Constructeur de la console de gestionnaire des commandes du tableur
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     * @param lesConsoles R�f�rence au groupe de consoles afin de pouvoir s'en
     *                          servir pour des calculs sur le tableur
     * @param leTableur R�f�rence permettant d'acc�der au tableur
     */
    public ConsoleGestionTableur(FenetrePrincipale laFenetre,
            Commandes lesConsoles, Tableur leTableur) {
        super(laFenetre);
        this.leTableur = leTableur;
        this.lesConsoles = lesConsoles;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de cette console
        this.reinitialisation();

        // On d�coupe la chaine avec les espaces
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));
        
        // On regarde quelle instruction est demand�e
        switch (this.getInstructions()[0].toString()) {
        case "CONT" : // Contenu d'une cellule ou d'une plage
            this.contenu();
            break;
        case "INIT" : // Initialisation d'une cellule
            this.initialisation();
            break;
        case "FORM" : // Initialisation avec une formule
            this.initFormule();
            break;
        case "COPIER" : // Copie des cellules
            this.copier();
            break;
        case "COPVAL" : // Copie la la valeur de la cellule
            this.copierValeur();
            break;
        case "RAZ" : // Remise � z�ro
            this.raz();
            break;
        case "AIDE" : // Aide
            // TODO
            break;
        }
        
        return this.getaRetourner();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #reinitialisation()
     */
    @Override
    public void reinitialisation() {
        this.setCommande(null);
        this.setInstructions(null);
        this.setErreurTrouvee(false);
        this.setaRetourner(null);
        this.setLieuMauvaisArgument(-1);
        this.plageCellules = null;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #rechercheErreur(int)
     */
    @Override
    public void rechercheErreur(int typeErreur) {
        int posErreur = 0;
        this.setErreurTrouvee(true); // Erreur d�clench�e
        StringBuilder tmpARetourner = new StringBuilder("  ");

        // On recherche la position de l'erreur dans la chaine originale
        for (int i = 0; i < this.getCommande().length()
                && posErreur < this.getLieuMauvaisArgument(); i++) {
            if (this.getCommande().charAt(i) == ' ') {
                posErreur++;
            }
            tmpARetourner.append(" ");
        }

        switch (typeErreur) {
        case ERREUR_NB_ARGUMENTS :
            this.setaRetourner(tmpARetourner.append("^\nErreur d'arguments : "
                    + "la commande " + this.getInstructions()[0] + " prend "                  
                    + nbArgumentsCommande() + " argument(s)").toString());
            break;
        case ERREUR_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'est pas une cellule ou une plage de cellules.");
            break;
        case ERREUR_ORDRE_PLAGE_MEMOIRES :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur de syntaxe : les bornes de la plage de cellules "
                            + "doivent �tre rang�es dans l'ordre.").toString());
            break;
        case ERREUR_VALEUR_ARGUMENT :
            this.setaRetourner(tmpARetourner.append(
                    "^\nErreur d'arguments : le deuxi�me argument "
                            + "doit �tre un r�el.").toString());
            break;
        }        
    }

    /**
     * Affiche soit le contenu de toutes les cellules (sans arguments), soit le
     * contenu des cellules sp�cifi�es. C'est-�-dire valeur + �ventuellement
     * formule.
     */
    private void contenu() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("CONT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageCellules != null) {
            StringBuilder aRetourner = new StringBuilder("");
            
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    aRetourner.append("\n"
                            + this.leTableur.contenuCellule(new int[]{i, j}));
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #initialisation()
     */
    @Override
    public void initialisation() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("INIT")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();
        
        /*
         * On r�cup�re le deuxi�me argument qui sera le contenu de la zone
         * s'il s'agit bien d'un double
         */
        String valeur = null; // Initialisation bidon
        if (estUnDouble(this.getInstructions()[2])) {
            // On contr�le si on peut enlev� la virgule s'il y en a une
            valeur = estUnEntier(this.getInstructions()[2]) ? Integer.toString(
                    (int) Double.parseDouble(this.getInstructions()[2])) 
                    : this.getInstructions()[2];
                    
        } else {
            // La valeur est mauvaise (2�me argument)
            this.setLieuMauvaisArgument(2);
            this.rechercheErreur(ERREUR_VALEUR_ARGUMENT);
            return;
        }
        
        // On garde une trace sur la console
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique l'initialisation si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On r�cup�re les coordonn�es en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});

                    // On affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, valeur);

                    aRetourner.append(tmpZone + " a �t� inintialis�e � "
                            + valeur + ".\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * Initialise une cellule ou une plage de cellules avec une formule
     */
    private void initFormule() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("FORM")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();      
        
        // On r�cup�re la formule et on la d�coupe pour la contr�ler
        this.formule = this.getCommande().replaceFirst(
                "FORM " + this.getCommande().split(" ")[1] + " " , "");
        System.out.println(this.formule);    
        
        StringBuilder aRetourner = new StringBuilder("");
        
        // Si la plage m�moire est correcte, on peut continuer
        if (this.plageCellules != null) {
            
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On r�cup�re les coordonn�es en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});
                    
                    // On effectue l'op�ration
                    aRetourner.append("\n" + this.lesConsoles.getTableur()
                           .traitementCommande(this.formule + " = " + tmpZone));
                    
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
    }

    /**
     * TODO comment method role
     */
    private void copier() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Copie la VALEUR de la cellule ou de la plage du premier argument dans la
     * cellule ou la plage du deuxi�me � condition que les dimensions des deux
     * arguments soient les m�mes
     */
    private void copierValeur() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("COPVAL")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();        
    }

    /**
     * Remet � 0 la cellule ou la plage cellule sp�cifi�e en argument
     */
    private void raz() {
        // On v�rifie d'abord qu'il y ait 2 arguments
        if (!verificationNombreArgument("RAZ")) {
            return;
        } // else le nombre d'argument est bon
        
        // On contr�le s'il n'y a qu'une zone m�moire ou une plage
        this.controlePlageMemoire();  
        
        StringBuilder aRetourner = new StringBuilder("");
        
        // On applique la remise � 0 si la plage est correcte
        if (this.plageCellules != null) {
                        
            for (int i = this.plageCellules[0][0];
                    i <= this.plageCellules[1][0]; i++) {
             
                for (int j = this.plageCellules[0][1];
                        j <= this.plageCellules[1][1]; j++) {
                    
                    // On r�cup�re les coordonn�es en chaines
                    String tmpZone = this.leTableur.
                            conversionCoordonneesEnChaine(new int[]{i, j});

                    // On affecte la valeur
                    this.leTableur.affectationValeur(tmpZone, "0");

                    aRetourner.append(tmpZone + " a �t� remis � 0.\n");
                }
            }
            this.setaRetourner(aRetourner.toString());
        }
        
    }
    
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #controlePlageMemoire()
     */
    @Override
    public void controlePlageMemoire() {
        
        if (this.getInstructions().length == 1) {
            // On l'applique sur toutes les plages m�moires
            this.plageCellules = new int[][]{ {0,0}, {19,25} };

        } else if (estUneMemoire(this.getInstructions()[1])) {
                        
            // La plage commence et fini au m�me endroit
            this.plageCellules = new int[][]{
                    this.leTableur.conversionChaineEnCoordonnees(
                            this.getInstructions()[1]),
                    this.leTableur.conversionChaineEnCoordonnees(
                            this.getInstructions()[1])};

        } else if (estUnePlageDeCellulesOrdonnees(this.getInstructions()[1])) {
            // On r�cup�re les coordonn�es de la plage
            this.plageCellules = this.leTableur.coordonneesPlageCorrecte(
                    this.getInstructions()[1]);

        } else { // Erreur dans la plage de cellules
            this.setLieuMauvaisArgument(1);
            if (estUnePlageDeCellules(this.getInstructions()[1])) {
                this.rechercheErreur(ERREUR_ORDRE_PLAGE_MEMOIRES);
            } else {
                this.rechercheErreur(ERREUR_PLAGE_MEMOIRES);
            }
        }
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #argumentsCommandeAttendus(java.lang.String)
     */
    @Override
    public boolean argumentsCommandeAttendus(String commande) {
        
        switch (commande.toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif
            return this.getInstructions().length <= 2;
        case "INIT" :       
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage m�moire et la valeur
            return this.getInstructions().length == 3;
        case "FORM" :
            // La commande, la plage m�moire et la formule
            return this.getInstructions().length >= 3;
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #nbArgumentsCommande()
     */
    @Override
    public String nbArgumentsCommande() {

        switch (this.getInstructions()[0].toString()) {
        case "CONT" :
        case "RAZ" :
            // La commande et la plage (facultatif) 
            return "0 ou 1";
        case "INIT" :       
        case "FORM" :
        case "COPIER" :
        case "COPVAL" :
            // La commande, la plage plage et la valeur
            return "2";
        }
        
        throw new IllegalArgumentException("Passage impossible");
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleGestionMemoire
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_CELLULE).matcher(aTester).matches();
    }
    
    /**
     * Contr�le si une chaine de caract�res est une plage de cellules
     * @param aTester Chaine � tester
     * @return true si la chaine est une plage de cellules, false sinon
     */
    private static boolean estUnePlageDeCellules(String aTester) {
        return Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches();
    }
    
    /**
     * Contr�le si une chaine de caract�res est une plage de cellules ordonn�es
     * @param aTester Chaine � tester
     * @return true si la chaine est une plage de cellules, false sinon
     */
    public boolean estUnePlageDeCellulesOrdonnees(String aTester) {
        
        if (Pattern.compile(REGEX_PLAGE_CELLULES).matcher(aTester).matches()) {
            int[][] coordonnees = leTableur.coordonneesPlageCorrecte(aTester);
            
            return (coordonnees[0][0] < coordonnees[1][0]
                    || (coordonnees[0][0] == coordonnees[1][0]
                            && coordonnees[0][1] < coordonnees[1][1]));
        }
        return false;
    }
}