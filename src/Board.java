import java.io.BufferedWriter;
import java.io.IOException;

public class Board {
    private int weight;
    private int height;
    private String[][] gameboard;

    public Board(int weight, int height){
        this.weight=weight;
        this.height=height;
        this.gameboard=new String[weight][height];
        //initialize the game board with empty spaces
        for(int i=0;i<height;i++){
            for(int k=0;k<weight;k++){
                this.gameboard[i][k]="  ";
            }
        }
    }

    public void printBoard(BufferedWriter writer) throws IOException {
        //upper and lower sides of the board with a star
        String stars="";
        for(int i=0;i<2*this.weight+2;i++){
            stars=stars+"*";
        }
        writer.write(stars+"\n");
        //print the elements of the board
        for(int i=0;i<this.height;i++){
            writer.write("*");
            for(int k=0;k<this.weight;k++){
                writer.write(gameboard[i][k]);
            }
            writer.write("*\n");
        }
        writer.write(stars+"\n\n");
    }

    //set a square with a character
    public void set(int row,int col,String character){
        this.gameboard[row][col]=character;
    }

    public String get(int row, int col){
        return this.gameboard[row][col];
    }

    public void deleteIndex(int row,int col){
        this.gameboard[row][col]="  ";
    }

    public void moveChar(String charId,int targetRow,int targetCol){
        for(int i=0; i<this.gameboard.length;i++){
            for(int k=0;k<this.gameboard[i].length;k++){
                if(this.gameboard[i][k].equals(charId)){
                    int myRow=i;
                    int myCol=k;
                    this.deleteIndex(i,k);
                    this.set(targetRow,targetCol,charId);
                }
            }
        }
    }

    public int[] findChar(String charId){
        int[] indexes = new int[2];
        for(int i=0; i<this.gameboard.length;i++){
            for(int k=0;k<this.gameboard[i].length;k++){
                if(this.gameboard[i][k].equals(charId)){
                     indexes[0]=i;
                     indexes[1]=k;
                }
            }
        }
        return indexes;
    }


}
