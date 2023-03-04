import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Character {
    private String id;
    private int HP;
    private int AP;
    private int maxMove;
    private int maxHP;
    public Character(String id){
        this.id=id;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getAP() {
        return AP;
    }

    public void setAP(int AP) {
        this.AP = AP;
    }

    public int getMaxMove() {
        return maxMove;
    }

    public void setMaxMove(int maxMove) {
        this.maxMove = maxMove;
    }

    public String getId() {
        return id;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public void setId(String id) {
        this.id = id;
    }

    //prints all characters to the console
    public static void printChars(ArrayList<Character> chars, BufferedWriter writer) throws IOException {
        for(Character ch:chars){
            if(ch!=null){
                writer.write(String.format("%s\t%d\t(%d)\n",ch.getId(),ch.getHP(),ch.getMaxHP()));
            }
        }
        writer.write("\n");
    }

    public String toString(){
        return this.getClass().getName();
    }
}
