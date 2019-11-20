package cgp;

public class InputParams {
    static InputParams singleton;
    private InputParams(){};

    public static synchronized InputParams getInstance() {
        if (singleton == null) {
            singleton = new InputParams();
        }
        return singleton;
    }

    /*
        Hard coded for now
     */
    private int columns = 3;
    private int rows = 2;

    public static InputParams getSingleton() {
        return singleton;
    }

    private float scaleFactors[] = {1.0f, 1.2f};

    public float[] getScaleFactors(){
        return scaleFactors;
    }


    private int width = 300;
    private int height = 200;
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }



    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }
}
