/* 
 * ConsoleCalcul.java                            12 avr. 2015
 * IUT info1 Groupe 3 2014-2015
 */
package minicalcul.programme.commandes;

import java.util.Arrays;
import java.util.regex.Pattern;

import minicalcul.fenetre.FenetrePrincipale;

/**
 * Objet repr�sentantant tous les traitements de la console
 * @author Cl�ment Zeghmati
 * @version 0.1
 */
public class ConsoleCalculSimple extends Console implements ConsoleCalcul {

    /**
     * Tableau de v�rit� des successeurs des caract�res possibles de :
     *  - 0 : double
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenth�se ouvrante
     *  - 3 : parenth�se fermante
     *  - 4 : =
     *  - 5 : zone m�moire ou cellule d'un tableur
     */
    public static final boolean[][] VERITE_SUCCESSEURS = {
        {false, true , false, true , true , false},
        {true , false, true , false, false, true },
        {true , false, true , false, false, true },
        {false, true , false, true , true , false},
        {false, false, false, false, false, true },
        {false, true , false, true , true , false} };
    
    /** Priorit� des op�rateurs */
    public static final String[] PRIORITE_OPERATEUR = {"/", "*", "+", "-"};
        
    /** 
     * Types des sous-chaines de la commande envoy�e 
     *  - 0 : double
     *  - 1 : operateur (+ - * /)
     *  - 2 : parenth�se ouvrante
     *  - 3 : parenth�se fermante
     *  - 4 : =
     *  - 5 : lettre majuscule repr�sentant une zone m�moire 
     */
    private int[] typesSousChaines;
    
    /** Zone m�moire � laquelle le r�sultat doit �tre affect� */
    private String zoneAffecte;
        
    /** Nombre de paires de parenth�ses dans l'expression envoy�e */
    private int nbParentheses;

    /**
     * Constructeur de la console de calculs
     * @param laFenetre R�f�rence permettant d'acc�der � la console et aux
     *                           zones m�moires
     */
    public ConsoleCalculSimple(FenetrePrincipale laFenetre) {
        this.setLaFenetre(laFenetre);
    }
 
    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #traitementCommande(java.lang.String)
     */
    @Override
    public String traitementCommande(String commande) {
        // On commence par r�initialiser l'�tat de cet objet
        this.reinitialisation();

        // On d�coupe la chaine avec les espaces en conservant l'originale
        this.setCommande(commande);
        this.setInstructions(commande.split(" "));

        // On v�rifie la syntaxe des sous-chaines 
        if (!this.verificationSyntaxe()) {
            /*
             * On recherche le lieux de l'erreur et on retourne la localisation
             * avec un message
             */
            this.rechercheErreur(ERREUR_SYNTAXE);
        }

        // On v�rifie le format de la chaine s'il n'y a pas eu d'erreur avant
        if (!this.isErreurTrouvee() && (!this.verificationFormat())) {
            this.rechercheErreur(ERREUR_FORMAT);
        }
        
        // Le nombre d'= doit �tre �gal � 1 car il doit y avoir une affectation.
        if (!this.isErreurTrouvee() && !this.verificationAffectation()) {
            this.rechercheErreur(ERREUR_AFFECTATION);
        }

        /*
         * S'il n'y a pas eu d'erreur avant et que la restauration s'est mal
         * pass�, on d�clenche une erreur. Si la restauration s'est bien pass�,
         * on peut continuer
         */
        if (!this.isErreurTrouvee() && !this.restaurationSauvegarde()) {
            this.rechercheErreur(ERREUR_INTIALISATION);
        }        

        // On peut faire le calcul s'il n'y a pas eu d'erreur avant
        if (!this.isErreurTrouvee()) {

            if (this.nbParentheses != 0) {
                this.calculExpressionParenthese();

                /*
                 * Tous les calculs entre parenth�se ont �t� fait et les 
                 * parenth�ses ont �t� enlev�es. Il reste donc une op�ration 
                 * simple que l'on effectue.
                 */
                this.calculExpressionSimple(0, this.longueurInstruction() - 1);

            } else {
                this.calculExpressionSimple(0, this.getInstructions().length-1);           
            }

            // On teste si le r�sultat est un entier pour eviter la virgule
            if (estUnEntier(this.getInstructions()[0])) {
                this.getInstructions()[0] = Integer.toString(
                        (int) Double.parseDouble(this.getInstructions()[0]));
            }

            /*
             * Si l'on doit faire une affectation, on la fait en mettant � jour
             * les zones m�moire. 
             */
            if (this.zoneAffecte != null) {
                this.getLaFenetre().getLaMemoire().affectationMemoire(
                        this.caseAAffecter(), this.getInstructions()[0]);
            }
        }

        // On retourne le r�sultat s'il n'y a pas d'erreur
        return !this.isErreurTrouvee() ? ("= " + this.getInstructions()[0])
                        : this.getaRetourner();
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#reinitialisation()
     */
    @Override
    public void reinitialisation() {
        this.setCommande(null);
        this.setInstructions(null);
        this.setErreurTrouvee(false);
        this.setaRetourner(null);
        this.setLieuMauvaisArgument(-1);
        this.typesSousChaines = null;
        this.zoneAffecte = null;
        this.nbParentheses = 0;           
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console#rechercheErreur(int)
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

        // On rajoute le type d'erreur
        if (typeErreur == ERREUR_SYNTAXE) {       
            this.setaRetourner(tmpARetourner.append(
             "^\nErreur de syntaxe : symbole \"" 
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" inconnu.");
        } else if (typeErreur == ERREUR_FORMAT) {
            if (this.nbParentheses == -1) {
                // Probl�me au niveau des parenth�ses
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " le nombre de parenth�ses ouvrantes et fermantes"
                        + " doit �tre �quivalent.").toString());
            } else {
                // Probl�me avec le tableau de v�rit�
                this.setaRetourner(tmpARetourner.append("^\nErreur de format :"
                        + " symbole innatendu.").toString());
            }
        } else if (typeErreur == ERREUR_INTIALISATION) {
            this.setaRetourner(tmpARetourner.append(
             "^\nErreur d'initialisation : \""
             + this.getInstructions()[this.getLieuMauvaisArgument()]).toString()
             + "\" n'a pas �t� initialis�.");
        }         
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#verificationSyntaxe()
     */
    @Override
    public boolean verificationSyntaxe() {
        
        // On v�rifie la syntaxe de chaque sous-chaine
        for (int i = 0; i < this.getInstructions().length; i++) {
            if (!(estUnDouble(this.getInstructions()[i])
                    || estUnCaractereValide(this.getInstructions()[i])
                    || estUneMemoire(this.getInstructions()[i]))) {
                // On garde l'indice du mauvais argument pour traiter l'erreur
                this.setLieuMauvaisArgument(i);
                return false;
            }
        }
        return true;
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#verificationFormat()
     */
    @Override
    public boolean verificationFormat() {
        /*
         * On v�rifie le dernier �l�ment qui ne peut �tre un signe op�ratoire 
         * ou un = et �galement le premier �l�ment qui ne peut �tre un =. S'il y
         * a une erreur, on retourne la position de la derni�re
         * sous-chaine.
         */
        if (this.getInstructions()[this.getInstructions().length - 1]
                        .equals("=") || estUnOperateur(this.getInstructions()
                                [this.getInstructions().length - 1])) {
            this.setLieuMauvaisArgument(this.getInstructions().length - 1);
            return false;
        }
        
        if (this.getInstructions()[0].equals("=")) {
            this.setLieuMauvaisArgument(0);
            return false;
        }
        
        /*
         * Types rencontr�s tel que : 
         *      - 0 : double
         *      - 1 : operateur (+ - * /)
         *      - 2 : parenth�se ouvrante
         *      - 3 : parenth�se fermante  
         *      - 4 : =
         *      - 5 : lettre majuscule repr�sentant une zone m�moire ou cellule
         */
        this.typesSousChaines = new int[this.getInstructions().length];

        int ouvrante = 0,
            fermante = 0;

        // On r�cup�re les types de chaque sous chaine pour v�rifier la syntaxe
        for (int i = 0; i < this.getInstructions().length; i++) {

            if (this.getInstructions()[i].charAt(0) == '(') {
                ouvrante++;
                this.typesSousChaines[i] = 2;
            } else if (this.getInstructions()[i].charAt(0) == ')') {
                fermante++;
                this.typesSousChaines[i] = 3;
            } else if (estUnOperateur(this.getInstructions()[i])) {
                // Op�rateur
                this.typesSousChaines[i] = 1;                
            } else if (estUnDouble(this.getInstructions()[i])) {
                // Double
                this.typesSousChaines[i] = 0;
            } else if (this.getInstructions()[i].charAt(0) == '=') {
                // Egale
                this.typesSousChaines[i] = 4;
            } else if (estUneMemoire(this.getInstructions()[i])) {
                // Zone m�moire
                this.typesSousChaines[i] = 5;
            }
        }
        
        // Si il n'y a pas autant d'ouvrante que de fermantes pas bon
        if (ouvrante != fermante) {
            this.setLieuMauvaisArgument(this.getInstructions().length);
            
            // On donne -1 afin de pouvoir g�rer l'erreur par la suite
            this.nbParentheses = -1;
            
            return false; // Inutile de continuer
        }
        this.nbParentheses = ouvrante; // On garde le nombre de paires
        
        // On contr�le maintenant avec le tableau de v�rit�
        for (int i = 0; i < this.getInstructions().length - 1; i++) {
            if (!VERITE_SUCCESSEURS
                    [this.typesSousChaines[i]][this.typesSousChaines[i+1]]) {
                // Mauvais successeur
                this.setLieuMauvaisArgument(i + 1);
                return false;
            }
        }
        
        // Le format est bon
        return true;
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #verificationAffectation()
     */
    @Override
    public boolean verificationAffectation() {
        /*
         * On contr�le qu'il n'y ait pas d'= ou s'il y en a 1, qu'il soit suivit
         * d'une lettre. S'il y a une affectation, on conserve la zone o� l'on
         * effectuera la sauvegarde
         */
        int compteur = this.nombreEgaleExpression();
        return compteur == 0 || (compteur == 1 && affectationCorrecte());
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #restaurationSauvegarde()
     */
    @Override
    public boolean restaurationSauvegarde() {
        
        // On peut s'arr�ter de rechercher lorsqu'on rencontre un =
        for (int i = 0; i < this.getInstructions().length
                && !this.getInstructions()[i].equals("="); i++) {
            if (estUneMemoire(this.getInstructions()[i])) {
                
                /*
                 * S'il s'agit d'une zone m�moire, on v�rifie si elle est 
                 * bien initialis�e
                 */
                if (this.estInitialisee(this.getInstructions()[i])) {
                    this.getInstructions()[i] = this.getLaFenetre()
                       .getLaMemoire().valeurMemoire(this.getInstructions()[i]);
                    
                } else { // Erreur : zone non initialis�e
                    this.setLieuMauvaisArgument(i);
                    return false;  // On sort ici car op�ration impossible
                }
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#affectationCorrecte()
     */
    @Override
    public boolean affectationCorrecte() {
        /*
         * S'il n'y a qu'un =, il se situe en avant derni�re position. Et
         * le dernier caract�re est une lettre 
         */
        if (this.typesSousChaines[this.typesSousChaines.length - 2] == 4 &&
                estUneMemoire(this.getInstructions()
                        [this.getInstructions().length - 1])) {
            // On garde la zone
            this.zoneAffecte = this.getInstructions()
                    [this.getInstructions().length - 1];
            return true;
        }
        this.setLieuMauvaisArgument(this.typesSousChaines.length - 2);
        return false;
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #calculExpressionParenthese()
     */
    @Override
    public void calculExpressionParenthese() {
        System.out.println(Arrays.toString(this.getInstructions()));
        // On le fait autant de fois qu'il y a de parenth�ses + 1        
        for (int i = this.nbParentheses; i > 0; i--) {
            
            // On utilise des variables pour localiser ce que l'on cherche
            int compteur = 0, debut = 0, fin = 0;
            
            // On cherche la derni�re parenth�se ouvrante
            for (int j = 0; j < this.getInstructions().length; j++) {
                
                if (this.getInstructions()[j].equals("(")) {
                    compteur++;
                    if (compteur == i) {
                        debut = j;
                        break;
                    }
                }
            }
            
            // Apr�s l'avoir trouv�e, on cherche la premi�re fermante qui suit
            for (int j = debut; j < this.getInstructions().length
                    && this.getInstructions()[j] != null; j++) {
                if (this.getInstructions()[j].equals(")")) {
                    fin = j;
                    break;
                }
            }
                      
            // On en tire une expression sans parenth�se que l'on calcule
            this.calculExpressionSimple(debut + 1, fin - 1);
            
            // On enl�ve les parenth�ses
            this.getInstructions()[debut] = this.getInstructions()[debut + 1];
            this.decallage(debut + 1);
            System.out.println(Arrays.toString(this.getInstructions()));

        }        
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *          #calculExpressionSimple(int, int)
     */
    @Override
    public void calculExpressionSimple(int debut, int fin) {
        // 4 comme le nombre d'op�rations possibles
        for (int i = 0; i < 4; i++) {
            
            int compteur = debut;
            while (compteur < fin && 
                    !this.getInstructions()[debut + 1].equals(")")) {
                
                // Si on trouve l'op�rateur que l'on recherche
                if (this.getInstructions()[compteur].
                        equals(PRIORITE_OPERATEUR[i])) {
                    System.out.println(Arrays.toString(this.getInstructions()));
                    this.operateurOperationOperateur(i, compteur);
                    
                    fin -= 2;           // On enl�ve 2
                    compteur = debut;   // On repart du d�but
                }
                compteur++;
            }
        }        
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul
     *                  #operateurOperationOperateur(int, int)
     */
    @Override
    public void operateurOperationOperateur(int operation, int position) {
        String resultat = null;
        
        switch (operation) {
        case 0 : // Division
            resultat = Double.toString(
                    Double.parseDouble(this.getInstructions()[position - 1])
                    /
                    Double.parseDouble(this.getInstructions()[position + 1]));
            break;
        case 1 : // Multiplication
            resultat = Double.toString(
                    Double.parseDouble(this.getInstructions()[position - 1])
                    *
                    Double.parseDouble(this.getInstructions()[position + 1]));
            break;
        case 2 : // Addition
            resultat = Double.toString(
                    Double.parseDouble(this.getInstructions()[position - 1])
                    +
                    Double.parseDouble(this.getInstructions()[position + 1]));
            break;
        case 3 : // Soustraction
            resultat = Double.toString(
                    Double.parseDouble(this.getInstructions()[position - 1])
                    -
                    Double.parseDouble(this.getInstructions()[position + 1]));
            break;
        
        }
        
        // On remplace l'op�rande de gauche par le r�sultat
        this.getInstructions()[position - 1] = resultat;
        
        // Puis on d�calle
        this.decallage(position);        
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#decallage(int)
     */
    @Override
    public void decallage(int position) {
        // On d�calle de deux cases
        int i;      
        for (i = position; i < this.getInstructions().length - 2
                && this.getInstructions()[i + 2] != null; i++) {
            this.getInstructions()[i] = this.getInstructions()[i+2];
        }
        
        // Et on efface les deux derni�res cases
        this.getInstructions()[i] = this.getInstructions()[i+1] = null;        
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#nombreEgaleExpression()
     */
    @Override
    public int nombreEgaleExpression() {
        int aRetourner = 0;
        
        for (int i = 0; i < this.typesSousChaines.length; i++) {
            if (this.typesSousChaines[i] == 4) {
                aRetourner++;
            }
        }
        return aRetourner;
    }



    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#caseAAffecter()
     */
    @Override
    public int caseAAffecter() {
        // A=65 --> 0 .. Z=90 --> 25
        return this.zoneAffecte.charAt(0) - 65;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.ConsoleCalcul#longueurInstruction()
     */
    @Override
    public int longueurInstruction() {
        int i;
        
        for (i = 0; i < this.getInstructions().length
                && this.getInstructions()[i] != null; i++) {
            if (this.getInstructions()[i] == null) {
                return i;
            }
        }
        return i;
    }

    /* (non-Javadoc)
     * @see minicalcul.programme.commandes.Console
     *                  #estUneMemoire(java.lang.String)
     */
    @Override
    public boolean estUneMemoire(String aTester) {
        return Pattern.compile(REGEX_ZONE_MEMOIRE).matcher(aTester).matches();
    }

    /**
     * Teste si une chaine de caract�re est un caract�re valide
     *  => ( ) + - * /
     * @param aTester Chaine � tester
     * @return true si la chaine correspond � un caract�re sp�cial d'une 
     *         op�ration, false sinon          
     */
    public static boolean estUnCaractereValide(String aTester) {
    
        return aTester.length() == 1 &&
                (aTester.charAt(0) == '('
                || aTester.charAt(0) == ')'
                || aTester.charAt(0) == '='
                || estUnOperateur(aTester));
    }
    
    /**
     * Acceseur � typesSousChaines
     * @return typesSousChaines 
     */
    public int[] getTypesSousChaines() {
        return typesSousChaines;
    }

    /**
     * Mutateur de typesSousChaines
     * @param typesSousChaines nouveau typesSousChaines
     */
    public void setTypesSousChaines(int[] typesSousChaines) {
        this.typesSousChaines = typesSousChaines;
    }

    /**
     * Acceseur � zoneAffecte
     * @return zoneAffecte 
     */
    public String getZoneAffecte() {
        return zoneAffecte;
    }

    /**
     * Mutateur de zoneAffecte
     * @param zoneAffecte nouveau zoneAffecte
     */
    public void setZoneAffecte(String zoneAffecte) {
        this.zoneAffecte = zoneAffecte;
    }

    /**
     * Acceseur � nbParentheses
     * @return nbParentheses 
     */
    public int getNbParentheses() {
        return nbParentheses;
    }

    /**
     * Mutateur de nbParentheses
     * @param nbParentheses nouveau nbParentheses
     */
    public void setNbParentheses(int nbParentheses) {
        this.nbParentheses = nbParentheses;
    }
}