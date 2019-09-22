import java.util.ArrayList;

public class Node {
    public ArrayList<Transition> links;
    public static int index;
    public boolean estInitial;
    public boolean estFinal;

    public Node(boolean estInitial, boolean estFinal){
        this.index = index++;
        this.links = new ArrayList();
        this.estInitial = estInitial;
        this.estFinal = estFinal;
    }

    public void addTransition(Transition transition){
        this.links.add(transition);
    }
}