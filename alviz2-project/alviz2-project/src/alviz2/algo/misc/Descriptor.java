/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alviz2.algo.misc;
import alviz2.graph.Node;

/**
 *
 * @author Karan
 */
public class Descriptor {
    private Node id;
    private boolean isSolved;
    private Double hValue;

    public Descriptor(Node id, boolean isSolved, Double hValue) {
        this.id = id;
        this.isSolved = isSolved;
        this.hValue = hValue;
    }

    public Node getId() {
        return id;
    }

    public void setId(Node id) {
        this.id = id;
    }

    public boolean isIsSolved() {
        return isSolved;
    }

    public void setIsSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }

    public Double gethValue() {
        return hValue;
    }

    public void sethValue(Double hValue) {
        this.hValue = hValue;
    }


    
}
