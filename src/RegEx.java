import java.util.Scanner;
import java.util.ArrayList;

import java.lang.Exception;

public class RegEx {
    //MACROS
    static final int CONCAT = 0xC04CA7;
    static final int ETOILE = 0xE7011E;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int PARENTHESEOUVRANT = 0x16641664;
    static final int PARENTHESEFERMANT = 0x51515151;
    static final int DOT = 0xD07;

    //REGEX
    private static String regEx;

    //CONSTRUCTOR
    public RegEx(){}


    public static Etat creeAutomateExempleEtape5(){
        Etat r = new Etat(true, false);
        Etat n1 = new Etat(false, false);
        Etat n2 = new Etat(false, false);
        Etat n3 = new Etat(false, false);
        Etat n4 = new Etat(false, false);
        Etat n5 = new Etat(false, false);
        Etat etatFinal = new Etat(false, true);

        Transition transitionS = new Transition((int)'S', n1);
        r.addTransition(transitionS);

        Transition transitionA = new Transition((int)'a', n2);
        Transition transitionR = new Transition((int)'r', n3);
        Transition transitionG = new Transition((int)'g', n4);
        n1.addTransition(transitionA);
        n1.addTransition(transitionR);
        n1.addTransition(transitionG);

        Transition transitionEsp1 = new Transition(-1, n1);
        Transition transitionO1 = new Transition((int)'o', n5);
        n2.addTransition(transitionEsp1);
        n2.addTransition(transitionO1);

        Transition transitionEsp2 = new Transition(-1, n1);
        Transition transitionO2 = new Transition((int)'o', n5);
        n3.addTransition(transitionEsp2);
        n3.addTransition(transitionO2);

        Transition transitionEsp3 = new Transition(-1, n1);
        Transition transitionO3 = new Transition((int)'o', n5);
        n4.addTransition(transitionEsp3);
        n4.addTransition(transitionO3);

        Transition transitionO4 = new Transition((int)'o', n5);
        n1.addTransition(transitionO4);

        Transition transitionN = new Transition((int)'n', etatFinal);
        n5.addTransition(transitionN);

        return r;
    }

    //MAIN
    public static void main(String arg[]) {
        /*System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        if (arg.length!=0) {
            regEx = arg[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regEx = scanner.next();
        }
        System.out.println("  >> Parsing regEx \""+regEx+"\".");
        System.out.println("  >> ...");

        if (regEx.length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
            for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
            System.out.println("].");
            try {
                RegExTree ret = parse();
                System.out.println("  >> Tree result: "+ret.toString()+".");
            } catch (Exception e) {
                System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
            }
        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Goodbye Mr. Anderson."); */

        // crée un automate orienté objet sur S(a|g|r)*on
        creeAutomateExempleEtape5();

        // crée un tableau représentant l'automate de S(a|g|r)*on
        // + minimisation
        exempleEtape5();
    }

    //FROM REGEX TO SYNTAX TREE
    private static RegExTree parse() throws Exception {
        //BEGIN DEBUG: set conditionnal to true for debug example
        if (false) throw new Exception();
        RegExTree example = exampleAhoUllman();
        if (false) return example;
        //END DEBUG

        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        for (int i=0;i<regEx.length();i++) result.add(new RegExTree(charToRoot(regEx.charAt(i)),new ArrayList<RegExTree>()));

        return parse(result);
    }
    private static int charToRoot(char c) {
        if (c=='.') return DOT;
        if (c=='*') return ETOILE;
        if (c=='|') return ALTERN;
        if (c=='(') return PARENTHESEOUVRANT;
        if (c==')') return PARENTHESEFERMANT;
        return (int)c;
    }
    private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
        while (containParenthese(result)) result=processParenthese(result);
        while (containEtoile(result)) result=processEtoile(result);
        while (containConcat(result)) result=processConcat(result);
        while (containAltern(result)) result=processAltern(result);

        if (result.size()>1) throw new Exception();

        return removeProtection(result.get(0));
    }
    private static boolean containParenthese(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==PARENTHESEFERMANT || t.root==PARENTHESEOUVRANT) return true;
        return false;
    }
    private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t: trees) {
            if (!found && t.root==PARENTHESEFERMANT) {
                boolean done = false;
                ArrayList<RegExTree> content = new ArrayList<RegExTree>();
                while (!done && !result.isEmpty())
                    if (result.get(result.size()-1).root==PARENTHESEOUVRANT) { done = true; result.remove(result.size()-1); }
                    else content.add(0,result.remove(result.size()-1));
                if (!done) throw new Exception();
                found = true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(parse(content));
                result.add(new RegExTree(PROTECTION, subTrees));
            } else {
                result.add(t);
            }
        }
        if (!found) throw new Exception();
        return result;
    }
    private static boolean containEtoile(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==ETOILE && t.subTrees.isEmpty()) return true;
        return false;
    }
    private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t: trees) {
            if (!found && t.root==ETOILE && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw new Exception();
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                result.add(new RegExTree(ETOILE, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }
    private static boolean containConcat(ArrayList<RegExTree> trees) {
        boolean firstFound = false;
        for (RegExTree t: trees) {
            if (!firstFound && t.root!=ALTERN) { firstFound = true; continue; }
            if (firstFound) if (t.root!=ALTERN) return true; else firstFound = false;
        }
        return false;
    }
    private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        boolean firstFound = false;
        for (RegExTree t: trees) {
            if (!found && !firstFound && t.root!=ALTERN) {
                firstFound = true;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root==ALTERN) {
                firstFound = false;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root!=ALTERN) {
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                subTrees.add(t);
                result.add(new RegExTree(CONCAT, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }
    private static boolean containAltern(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==ALTERN && t.subTrees.isEmpty()) return true;
        return false;
    }
    private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        RegExTree gauche = null;
        boolean done = false;
        for (RegExTree t: trees) {
            if (!found && t.root==ALTERN && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw new Exception();
                found = true;
                gauche = result.remove(result.size()-1);
                continue;
            }
            if (found && !done) {
                if (gauche==null) throw new Exception();
                done=true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(gauche);
                subTrees.add(t);
                result.add(new RegExTree(ALTERN, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }
    private static RegExTree removeProtection(RegExTree tree) throws Exception {
        if (tree.root==PROTECTION && tree.subTrees.size()!=1) throw new Exception();
        if (tree.subTrees.isEmpty()) return tree;
        if (tree.root==PROTECTION) return removeProtection(tree.subTrees.get(0));

        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        for (RegExTree t: tree.subTrees) subTrees.add(removeProtection(t));
        return new RegExTree(tree.root, subTrees);
    }

    //EXAMPLE
    // --> RegEx from Aho-Ullman book Chap.10 Example 10.25
    private static RegExTree exampleAhoUllman() {
        RegExTree a = new RegExTree((int)'a', new ArrayList<RegExTree>());
        RegExTree b = new RegExTree((int)'b', new ArrayList<RegExTree>());
        RegExTree c = new RegExTree((int)'c', new ArrayList<RegExTree>());
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(c);
        RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(b);
        subTrees.add(cEtoile);
        RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(a);
        subTrees.add(dotBCEtoile);
        return new RegExTree(ALTERN, subTrees);
    }

    public static void exempleEtape5(){
        System.out.println("S(a|g|r)*on");
        int res[][] = new int[7][8];

        res[0][0]=2;res[0][6]=1;
        res[1][1]=3;res[1][2]=4;res[1][3]=5;res[1][4]=6;
        res[2][1]=2;res[2][2]=2;res[2][3]=2;res[2][4]=6;
        res[3][1]=2;res[3][2]=2;res[3][3]=2;res[3][4]=6;
        res[4][1]=2;res[4][2]=2;res[4][3]=2;res[4][4]=6;
        res[5][5]=7;
        res[6][7]=1;

        System.out.println("----------------");
        System.out.println("S a r g o n I F");
        System.out.println("----------------");
        for(int i=0; i<7; i++) {
            for (int j = 0; j < 8; j++)
                System.out.print(res[i][j] + " ");
            System.out.println();
        }


        System.out.println();
        System.out.println("Minimisé");

        int minimise[][] = new int[5][8];

        minimise[0][0]=2;minimise[0][6]=1;
        minimise[1][1]=3;minimise[1][2]=3;minimise[1][3]=3;minimise[1][4]=4;
        minimise[2][1]=2;minimise[2][2]=2;minimise[2][3]=2;minimise[2][4]=4;
        minimise[3][5]=5;
        minimise[4][7]=1;

        System.out.println("----------------");
        System.out.println("S a r g o n I F");
        System.out.println("----------------");
        for(int i=0; i<5; i++) {
            for (int j = 0; j < 8; j++)
                System.out.print(minimise[i][j] + " ");
            System.out.println();
        }


        System.out.println();
    }
}



//UTILITARY CLASS
class RegExTree {
    protected int root;
    protected ArrayList<RegExTree> subTrees;
    public RegExTree(int root, ArrayList<RegExTree> subTrees) {
        this.root = root;
        this.subTrees = subTrees;
    }
    //FROM TREE TO PARENTHESIS
    public String toString() {
        if (subTrees.isEmpty()) return rootToString();
        String result = rootToString()+"("+subTrees.get(0).toString();
        for (int i=1;i<subTrees.size();i++) result+=","+subTrees.get(i).toString();
        return result+")";
    }
    private String rootToString() {
        if (root==RegEx.CONCAT) return ".";
        if (root==RegEx.ETOILE) return "*";
        if (root==RegEx.ALTERN) return "|";
        if (root==RegEx.DOT) return ".";
        return Character.toString((char)root);
    }
    /*private int[][] mergeConcat(int taille1, int[][] t1, int taille2, int[][] t2){
        int[][] res = new int[taille1+taille2][130];

        int iajout =0;
        int numero =1;
        int i, j;
        for(i=0; i<taille1; i++) {
            for (j = 0; j < 130; j++) {
                res[i][j] = t1[i][j];
                if(t1[i][j]>numero)
                    numero=t1[i][j];
            }
            if(t1[i][FINAL]==1) {
                res[i][FINAL] = 0;
                //res[]; continuer ici il manque à créer les transitions nouvelles
            }
        }

        for(i=0; i<taille2; i++)
            for (j = 0; j < 130; j++)
                if(t2[i][j]>0)
                    res[i+taille1][j] = t2[i][j]+numero;
                // ici aussi

        return res;
    }

    private int INITIAL=127;
    private int FINAL=128;*/

    /*public int[][] regExTreeToAutomata(){
        int numero =2;

        if (root==RegEx.CONCAT) return mergeConcat();
        else if (root==RegEx.ETOILE) return mergeEtoile();
        else if (root==RegEx.ALTERN) return mergeAltern();
        else {
            int taille=2;
            int[][] transitions= new int[2][130];

            t[0][root]=numero++;
            t[0][INITIAL]=1;
            t[1][FINAL]=1;
        }

        return transitions;
    }*/
}