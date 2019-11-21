package capturemate;

import capturemate.functions.Function;

public class Node {
    Function strategy;

    public Node(Function f) {
        this.strategy = f;
    }
    public Node() {}

}
