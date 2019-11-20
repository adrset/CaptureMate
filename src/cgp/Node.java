package cgp;

import cgp.functions.Function;

public class Node {
    Function strategy;

    public Node(Function f) {
        this.strategy = f;
    }
    public Node() {}

}
