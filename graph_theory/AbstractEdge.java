package template.graph_theory;

/**
 * Created by dy on 17-1-20.
 */
public abstract class AbstractEdge {
    private boolean visited = false;
    public int getId() {return -1;}

    public int getCost() {throw new UnsupportedOperationException();}
    public int getFlow() {return getInitialCapacity() - getCapacity();}
    public int getInitialCapacity() {throw new UnsupportedOperationException();}
    public int getCapacity() {throw new UnsupportedOperationException();}
    public void setCapacity(int capacity) {throw new UnsupportedOperationException();}
    public AbstractEdge getReversalEdge() {throw new UnsupportedOperationException();}
    public void setReversalEdge(AbstractEdge reversalEdge) {throw new UnsupportedOperationException();}
    public int getFrom() {throw new UnsupportedOperationException();}
    public int getTo() {throw new UnsupportedOperationException();}

    public boolean getVisited() {return visited;}
    public void setVisited() {visited = true;}
    public void notVisited() {visited = false;}


    public int other(int v) {
        if (v == getFrom()) return getTo();
        if (v == getTo()) return getFrom();
        throw new IllegalArgumentException();
    }
    @Override
    public String toString() {
        return null;
    }
}
