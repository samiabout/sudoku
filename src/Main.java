/**
 * Created by sami- on 29/09/2017.
 */
public class Main {

    public static void main(String[] args) {

        //Sudoku sudoku=new Sudoku("problems/4/2.sd",false, 0);
        for (int i = 1; i <=71 ; i++) {
            System.out.print(i+" : ");
            for (int j = 1; j <=10 ; j++) {
                Sudoku sudoku=new Sudoku("problems/"+i+"/"+j+".sd",true, 3);
            }
            System.out.println();
        }



    }
}
