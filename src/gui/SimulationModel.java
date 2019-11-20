package gui;

import cgp.InputParams;
import cgp.Node;

public class SimulationModel {

    public Node cartesian[][];
    private int columns;
    private int rows;

    public SimulationModel(InputParams params) {
        this.columns = params.getColumns();
        this.rows    = params.getRows();
        this.cartesian = new Node[columns][rows];
        for (int i = 0; i < this.cartesian.length; i++) {
            for (int j = 0; j < this.cartesian[i].length; j++) {
                this.cartesian[i][j] = new Node();
            }
        }
    }
}
