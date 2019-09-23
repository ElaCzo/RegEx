import java.util.ArrayList;

public class Etat {
    public ArrayList<Transition> links;
    public static int index;
    public boolean estInitial;
    public boolean estFinal;

    public Etat(boolean estInitial, boolean estFinal){
        this.index = index++;
        this.links = new ArrayList();
        this.estInitial = estInitial;
        this.estFinal = estFinal;
    }

    public void addTransition(Transition transition){
        this.links.add(transition);
    }
}