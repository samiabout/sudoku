import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * Created by sami- on 29/09/2017.
 *
 * This is core CSP solver
 */
public class Sudoku {
    private int[][] grid;
    private Cell[][] gridCells;
    boolean forwardChecking;
    int heuristic;
    int assignments;

    public Sudoku(String filePath,boolean forwardChecking,int heuristic,boolean disp) {
        this.heuristic=heuristic;
        this.forwardChecking=forwardChecking;
        this.assignments=0;
        this.grid = new int[9][9];
        this.gridCells=new Cell[9][9];
        List<String> lines = null;
        try {//parser
            lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 9; i++) {
            String line[]=lines.get(i).split(" ");
            for (int j = 0; j < 9 ; j++) {
                grid[i][j]=Integer.parseInt(line[j]);
            }
            String[] node = lines.get(i+1).split(" ");
        }
        //disp();
        boolean terminate=true;
        try{
            if (forwardChecking){
                forwardChecking(0,0,addLegalValues());
            }else {
                backtrackingSearch(0,0,new Cell());
            }

        }catch (StackOverflowError e){
            System.out.print("SOF ; ");
            terminate=false;
           //System.out.println(e);
        }catch (Exception e){
        }
        if(disp){
            disp();
        }
        if(terminate){
            System.out.print(assignments+" ; ");
        }
    }


    /******** backtracking ***********/

    /*
 Forward checking + backtracking and backtracking alone are resolved separately
    *
    *
    *
    *backtracking (without forward checking) mainly works on two recursive methods: the first one is used
    * to choose the next empty cell, to add a value on it
    * The second try to add the value or order a backtrack if necessary
     */
    private void backtrackingSearch(int iTest, int jTest,Cell parent){//search next empty cell
        /*if (iTest==9 ){//sudoku resolved
            int breakRecursion=1/0;//we cause an exception to quit the double recursion
        }*/
        if(grid[iTest][jTest]==0){//found next empty cell
            backtrackingSearch(new Cell(iTest,jTest,0,parent));
        }
        if(jTest==8){//try next cell
            backtrackingSearch(iTest+1,0,parent);
        }
        backtrackingSearch(iTest,jTest+1,parent);
    }
    private void backtrackingSearch(Cell cell){//test cell

        int iTest=cell.getI();
        int jTest=cell.getJ();
        int valueTest=cell.getValue()+1;
        if(valueTest==10){//we tried all the values so we have to backtrack
            assignments++;
            grid[cell.getParent().getI()][cell.getParent().getJ()]=0;
            //disp();
            cell.getParent().setValue(cell.getParent().getValue()+1);
            backtrackingSearch(cell.getParent());
        }
        if(checkAdd(iTest,jTest,valueTest)){//we can add a value
            assignments++;
            grid[iTest][jTest]=valueTest;
            //disp();
            backtrackingSearch(iTest,jTest,cell);
        }
        assignments++;
        cell.setValue(cell.getValue()+1);//we try with next value
        backtrackingSearch(cell);
    }

    private boolean checkAdd(int iTest, int jTest,int value){//test if we can add value at coordinate iTest jTest
        if (grid[iTest][jTest]!=0){
            return false;
        }
        for (int u = 0; u < 9 ; u++) {//test lines and collums
            if(grid[u][jTest]==value){
                return false;
            }
            if(grid[iTest][u]==value){
                return false;
            }
        }
        for (int i = (iTest/3)*3; i < (iTest/3)*3+3; i++) {//test the subGrid
            for (int j = (jTest/3)*3; j < (jTest/3)*3+3; j++) {
                if (grid[i][j]==value){
                    return false;
                }
            }
        }
        return true;
    }


    /******** Forward checking ***********/

    /*
    *
    * Forward checking + backtracking and bactracking alone are resolved separately
    *
    *Forward checking mainly works on two recursive methods: the first one is used
    * to choose on wich cell we will try to add a value
    * The second try to add the value or order a backtrack if necessary
    *
     */




    private void forwardChecking(int iTest, int jTest,Cell parent){//search next empty cell
        if (iTest==9 ){//sudoku resolved
            int breakRecursion=1/0;//we cause an exception to quit the double recursion
        }
        if(grid[iTest][jTest]==0){//found next empty cell
            updateLegalValues(parent.getGrid());
            forwardChecking(new Cell(iTest,jTest,parent,grid),0);
        }
        if(heuristic==0){//no heuristic
            if(jTest==8){//try next cell
                forwardChecking(iTest+1,0,parent);
            }
            forwardChecking(iTest,jTest+1,parent);
        }
       //updateLegalValues(parent.getGrid());
        if(heuristic==1){//most constrained value
            int legalValues=10;
            int ih=-1;
            int jh=-1;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(grid[i][j]==0 && gridCells[i][j].getLegalValues().size()<legalValues){
                        legalValues=gridCells[i][j].getLegalValues().size();
                        ih=i;
                        jh=j;
                    }
                }
            }
            forwardChecking(ih,jh,parent);
        }
        if(heuristic==2){//Is not working
            int constrains=-1;
            int ih=-1;
            int jh=-1;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(grid[i][j]==0 && constrains(i,j)>constrains){
                        constrains=constrains(i,j);
                        ih=i;
                        jh=j;
                    }
                }
            }
            forwardChecking(ih,jh,parent);
        }
        if(heuristic==3){//most constraining value
            int constrains=25;
            int ih=-1;
            int jh=-1;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if(grid[i][j]==0 && constrains(i,j)<constrains){
                        constrains=constrains(i,j);
                        ih=i;
                        jh=j;
                    }
                }
            }
            forwardChecking(ih,jh,parent);
        }

    }


    private void forwardChecking(Cell cell,int value){//test cell
        int iTest=cell.getI();
        int jTest=cell.getJ();
        if(gridCells[iTest][jTest].getLegalValues().size()==0  ||
                (value!=0 && gridCells[iTest][jTest].getLegalValues().indexOf(value)+1==gridCells[iTest][jTest].getLegalValues().size() )
                ){//we tried all the values so we have to backtrack(list size = 0 or we tried the last element of the list
            assignments++;
            //disp();
            int valueTest=grid[cell.getParent().getI()][cell.getParent().getJ()];
            grid[cell.getParent().getI()][cell.getParent().getJ()]=0;
            updateLegalValues(grid);
            //gridCells[cell.getParent().getI()][cell.getParent().getJ()].removeFirstValue();
            forwardChecking(cell.getParent(),valueTest);
        }else {//we can add a value
            assignments++;
            //disp();
            int valueTest=gridCells[cell.getI()][cell.getJ()].getLegalValues().get(0);

            if(value!=0){//second or more try on the cell : we take next value
                valueTest=gridCells[iTest][jTest].getLegalValues().get(
                          gridCells[iTest][jTest].getLegalValues().indexOf(value) + 1
                        );
            }
            grid[iTest][jTest] = valueTest;
            cell.getGrid()[iTest][jTest] = valueTest;
            //disp();
            forwardChecking(iTest, jTest, cell);
        }
    }

    private int constrains(int ih, int jh) {//used for the heuristics : calculate how constraining is a variable
        int constrains=0;
        for (int u = 0; u < 9 ; u++) {//test lines and collums
            if(grid[u][jh]==0){
                constrains++;
            }
            if(grid[ih][u]==0){
                constrains++;
            }
        }
        for (int i = (ih/3)*3; i < (ih/3)*3+3; i++) {//test the subGrid
            for (int j = (jh/3)*3; j < (jh/3)*3+3; j++) {
                if(i!=ih && j!=jh && grid[i][j]==0)
                    constrains++;
            }
        }
        return constrains;
    }

    private Cell addLegalValues(){//find the next unassigned cell (when there is no heuristic)
        Cell firstEmptyCell=null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(grid[i][j]==0){
                    gridCells[i][j] = new Cell(i,j,false,grid);
                    if (firstEmptyCell==null){
                        firstEmptyCell=gridCells[i][j];
                    }
                    for (int v = 1; v < 10; v++) {
                        if(checkAdd(i,j,v)){
                            gridCells[i][j].addLegalValue(v);
                        }
                    }
                }
                else {
                    gridCells[i][j] = new Cell(i,j,true);

                }
            }
        }
        return firstEmptyCell;
    }

    private void updateLegalValues(int[][] grid){//update the possible values of the grid, after adding a value
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if(!gridCells[i][j].isInitialValue()){
                    gridCells[i][j] = new Cell(i,j,false);
                    for (int v = 1; v < 10; v++) {
                        if(checkAdd(i,j,v,grid)){
                            gridCells[i][j].addLegalValue(v);
                        }
                    }
                }
            }
        }
    }


    private boolean checkAdd(int iTest, int jTest,int value,int[][] gridTest){//test if we can add value at coordinate iTest jTest, this one is used for forward checking
        if (gridTest[iTest][jTest]!=0){
            return false;
        }
        for (int u = 0; u < 9 ; u++) {//test lines and collums
            if(gridTest[u][jTest]==value){
                return false;
            }
            if(gridTest[iTest][u]==value){
                return false;
            }
        }
        for (int i = (iTest/3)*3; i < (iTest/3)*3+3; i++) {//test the subGrid
            for (int j = (jTest/3)*3; j < (jTest/3)*3+3; j++) {
                if (gridTest[i][j]==value){
                    return false;
                }
            }
        }
        return true;
    }




    public void disp(){//displays the grid
        System.out.println("--------------------");
        for (int i = 0; i < 9; i++) {
            System.out.println();
            for (int j = 0; j < 9 ; j++) {
                System.out.print(grid[i][j]+" ");
            }
        }
    }
}
