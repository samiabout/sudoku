import java.util.ArrayList;
import java.util.List;

/**
 * Created by sami- on 29/09/2017.
 *
 * This class describes on cell of the grid
 */
public class Cell {//list of added value, recorded to allow backtracking
    private int i;
    private int j;
    private int value;
    private List<Integer> legalValues;
    private Cell parent;
    private boolean initialValue;//value was already given (used forward checking)
    private int[][] grid;
    private List<Cell> gridCell;


    public Cell() {

    }

    public Cell(int i, int j, int value, Cell parent) {//backtracking
        this.i = i;
        this.j = j;
        this.value = value;
        this.parent = parent;
    }

    public Cell(int i, int j, Cell parent,int[][] grid) {//backtracking
        this.i = i;
        this.j = j;
        this.parent = parent;
        this.grid=new int[9][9];
        for (int k = 0; k < 9; k++) {
            for (int l = 0; l < 9; l++) {
                this.grid[k][l]=grid[k][l];
            }
        }
    }


    public Cell(int i, int j, boolean initialValue,int[][] grid) {//forwardchecking
        this.i = i;
        this.j = j;
        legalValues=new ArrayList<>();
        this.initialValue=initialValue;
        this.grid=new int[9][9];
        for (int k = 0; k < 9; k++) {
            for (int l = 0; l < 9; l++) {
                this.grid[k][l]=grid[k][l];
            }
        }
    }

    public Cell(int i, int j, boolean initialValue) {//forwardchecking
        this.i = i;
        this.j = j;
        legalValues=new ArrayList<>();
        this.initialValue=initialValue;
    }

    public void addLegalValue(int value){
        legalValues.add(value);
    }

    /*public void removeFirstValue(){
        legalValues.remove(0);
    }*/

    public List<Integer> getLegalValues(){
        return legalValues;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Cell getParent() {
        return parent;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public boolean isInitialValue() {
        return initialValue;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

}
