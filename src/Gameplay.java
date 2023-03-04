import java.io.*;
import java.util.*;
class MoveSequenceException extends Exception{
    MoveSequenceException(){
    }
}
public class Gameplay {
    String initialFile;
    String commandFile;
    String outputFile;
    Board board = null;
    ArrayList<Character> chars;
    BufferedWriter writer;

    public Gameplay(String initialFile,String commandFile,String outputFile) throws IOException {
        this.initialFile=initialFile;
        this.commandFile=commandFile;
        this.outputFile=outputFile;
        this.chars=null;
        this.writer = new BufferedWriter(new FileWriter(this.outputFile,false));
    }

    public Gameplay(){
    }

    public void launch() throws IOException {
        //read the initial file and initialize the board and characters
        FileInputStream fis=new FileInputStream(initialFile);
        Scanner sc=new Scanner(fis);    //file to be scanned
        ArrayList<String> charLines = new ArrayList<>();
        //returns true if there is another line to read
        int weight=0;
        int height=0;
        while(sc.hasNextLine()) {
            switch (sc.nextLine()){
                case "BOARD":
                    String[] size=sc.nextLine().split("x");
                    weight=Integer.parseInt(size[0]);
                    height=Integer.parseInt(size[1]);
                    break;
                case "CALLIANCE":
                    while(sc.hasNextLine()){
                        String calline = sc.nextLine();
                        if(calline.isEmpty()){
                            break;
                        }else{
                            charLines.add(calline);
                        }
                    }
                    break;
                case "ZORDE":
                    while(sc.hasNextLine()){
                        String zordeline = sc.nextLine();
                        if(zordeline.isEmpty()){
                            break;
                        }else{
                            charLines.add(zordeline);
                        }
                    }
                    break;
                default:
                    break;
            }    //returns the line that was skipped
        }
        sc.close();     //closes the scanner
        //create a board
        board = new Board(weight,height);
        placeChars(charLines);
        printGame();


    }
    //print the game board and characters
    private void printGame() throws IOException {
        board.printBoard(this.writer);
        Character.printChars(this.chars,this.writer);
    }

    //start the game and run the commands
    public void start() throws IOException {
        //read command file and make movement
        FileInputStream fis=new FileInputStream(commandFile);
        Scanner sc=new Scanner(fis);
        while(sc.hasNextLine()){
            String[] line = sc.nextLine().split(" ");
            String charId= line[0];
            int[] moves=new int[line[1].split(";").length];
            int i=0;
            for(String move: line[1].split(";")){
                moves[i++]=Integer.parseInt(move);
            }
            //move the character
            for(i=0;i<chars.size();i++){//find the character
                if(chars.get(i).getId().equals(charId)){
                    if(isMovementValid(i,moves)) {
                        move(charId,moves);
                    }else{
                        try {
                            throw new MoveSequenceException();
                        } catch (MoveSequenceException e) {
                            this.writer.write("Error : Move sequence contains wrong number of move steps. Input line ignored.\n\n");
                        }
                        //writer.write("Error : Move sequence contains wrong number of move steps. Input line ignored.\n\n");

                    }
                }
            }
            if(isGameOver()){
                this.writer.close();
                sc.close();
                return;
            }
        }
        this.writer.close();
        sc.close();
    }
    //check if game is over
    private boolean isGameOver() throws IOException {
        int calliances=0;
        int zordes=0;
        for(int i=0;i<this.chars.size();i++){
            if(this.chars.get(i) instanceof Zorde){
                zordes++;
            }else if(this.chars.get(i) instanceof Calliance){
                calliances++;
            }
        }
        if(calliances==0 || zordes==0){
            this.writer.write("Game Over\n");
            if(calliances==0){
                this.writer.write("Zorde Wins");
            }else if(zordes==0){
                this.writer.write("Callicance Wins");
            }
            return true;
        }
        return false;
    }

    public void move(String charId, int[] moves) throws IOException {
        Boolean isEveryStep = isEveryStep(charId);
        if(charId.startsWith("O")){
            heal(charId);
        }

        for(int col=0;col<moves.length;col++){//get moves 2 by 2
            int addCol=moves[col];
            int addRow=moves[++col];
            try{
                int myRow=this.board.findChar(charId)[0];
                int myCol=this.board.findChar(charId)[1];
                int targetRow=myRow+addRow;
                int targetCol=myCol+addCol;
                if(this.board.get(targetRow,targetCol).equals("  ")){//if target row is empty
                    this.board.moveChar(charId,targetRow,targetCol);
                }else{
                    String targetCharId=this.board.get(targetRow,targetCol);
                    if(isEnemy(charId,targetCharId)){//if target and char are enemy
                        fightToDeath(charId,targetCharId);//then fight to the death
                        printGame();
                        return;//stop moving
                    }else{
                        return;//stop moving
                    }
                }

            }catch (ArrayIndexOutOfBoundsException e){
                if(col!=1){//print if
                    printGame();
                }
                this.writer.write("Error : Game board boundaries are exceeded. Input line ignored.\n\n");
                return;
            }finally {
                if(charId.startsWith("E") && col==moves.length-1){//instead of last attack elf range attack
                    rangeAttack(charId);
                    printGame();
                    return;
                }


            }
            if(isEveryStep){//attacks every step
                attack(charId);
            }
        }
        if(!isEveryStep){
            attack(charId);
        }
        printGame();

    }
    //elfes range attack
    private void rangeAttack(String charId) {

        int myRow=this.board.findChar(charId)[0];
        int myCol=this.board.findChar(charId)[1];
        for(int addRow=-2;addRow<=2;addRow++){
            for(int addCol=-2;addCol<=2;addCol++){
                if(addRow==0 && addCol==0) continue;//if target is characters itself
                int targetRow=myRow+addRow;
                int targetCol=myCol+addCol;
                try{
                    String targetId=this.board.get(targetRow,targetCol);
                    if(targetId!="  "){
                        int charIndex=0;
                        for(int i=0;i< this.chars.size();i++){
                            if(this.chars.get(i).getId().equals(charId)){
                                charIndex=i;
                            }
                        }
                        int targetIndex=0;
                        for(int i=0;i< this.chars.size();i++){
                            if(this.chars.get(i).getId().equals(targetId)){
                                targetIndex=i;
                                break;
                            }
                        }
                        if(isEnemy(charId,targetId)){
                            Elf elf = (Elf) this.chars.get(charIndex);
                            int charAP=elf.getRangedAP();
                            int targetHP=this.chars.get(targetIndex).getHP();
                            targetHP-=charAP;
                            this.chars.get(targetIndex).setHP(targetHP);
                            if(this.chars.get(targetIndex).getHP()<=0){
                                this.board.deleteIndex(targetRow,targetCol);
                                this.chars.remove(targetIndex);
                            }
                        }

                    }

                }catch (ArrayIndexOutOfBoundsException e){
                    continue;
                }
            }
        }

    }
    //Ork's heal skill
    private void heal(String charId) {
        int charIndex=0;
        for(int i=0;i< this.chars.size();i++){
            if(this.chars.get(i).getId().equals(charId)){
                charIndex=i;
            }
        }
        int myRow=this.board.findChar(charId)[0];
        int myCol=this.board.findChar(charId)[1];
        for(int addRow=-1;addRow<2;addRow++){
            for(int addCol=-1;addCol<2;addCol++){
                int targetRow=myRow+addRow;
                int targetCol=myCol+addCol;
                try{
                    String targetId=this.board.get(targetRow,targetCol);
                    if(targetId!="  "){
                        int targetIndex=0;
                        for(int i=0;i< this.chars.size();i++){
                            if(this.chars.get(i).getId().equals(targetId)){
                                targetIndex=i;
                                break;
                            }
                        }

                        if(!isEnemy(charId,targetId)){
                            Ork ork = (Ork) this.chars.get(charIndex);
                            int healAmount= ork.getHealPoints();
                            int targetHP=this.chars.get(targetIndex).getHP();
                            if((targetHP+healAmount)>this.chars.get(targetIndex).getMaxHP()){
                                targetHP=this.chars.get(targetIndex).getMaxHP();
                            }else{
                                targetHP+=healAmount;
                            }
                            this.chars.get(targetIndex).setHP(targetHP);
                        }

                    }

                }catch (ArrayIndexOutOfBoundsException e){
                    continue;
                }
            }
        }
    }
    //character step attack
    private void attack(String charId) {
        int myRow=this.board.findChar(charId)[0];
        int myCol=this.board.findChar(charId)[1];
        for(int addRow=-1;addRow<=1;addRow++){
            for(int addCol=-1;addCol<=1;addCol++){
                if(addRow==0 && addCol==0) continue;//if target is characters itself
                int targetRow=myRow+addRow;
                int targetCol=myCol+addCol;
                try{
                    String targetId=this.board.get(targetRow,targetCol);
                    if(targetId!="  "){
                        int targetIndex=0;
                        for(int i=0;i< this.chars.size();i++){
                            if(this.chars.get(i).getId().equals(targetId)){
                                targetIndex=i;
                                break;
                            }
                        }

                        int charIndex=0;
                        for(int i=0;i< this.chars.size();i++){
                            if(this.chars.get(i).getId().equals(charId)){
                                charIndex=i;
                            }
                        }
                        if(isEnemy(charId,targetId)){
                            int charAP=this.chars.get(charIndex).getAP();
                            int targetHP=this.chars.get(targetIndex).getHP();
                            targetHP-=charAP;
                            this.chars.get(targetIndex).setHP(targetHP);
                            if(this.chars.get(targetIndex).getHP()<=0){
                                this.board.deleteIndex(targetRow,targetCol);
                                this.chars.remove(targetIndex);
                            }
                        }

                    }

                }catch (ArrayIndexOutOfBoundsException e){
                    continue;
                }
            }
        }

    }

    //returns 1 if character attacks every step
    private Boolean isEveryStep(String charId) {
        boolean isEveryStep= true;
        for(int i=0;i< this.chars.size();i++){
            if(this.chars.get(i).getId().equals(charId)){
                switch (this.chars.get(i).toString()){
                    case "Ork": isEveryStep=false; break;
                    case "Troll": isEveryStep=false; break;
                    case "Human": isEveryStep=false; break;
                    default:break;
                }
            }

        }
        return isEveryStep;
    }
    //two characters fight to the death
    private void fightToDeath(String charId, String targetCharId) {
        int charIndex=0;
        int targetIndex=0;
        for(int i=0;i< this.chars.size();i++){
            if(this.chars.get(i).getId().equals(charId)){
                charIndex=i;
            }else if(this.chars.get(i).getId().equals(targetCharId)){
                targetIndex=i;
            }
        }

        int[] targetPos = this.board.findChar(targetCharId);
        int[] charPos = this.board.findChar(charId);
        //fight to death starts
        if(this.chars.get(charIndex).getHP()==this.chars.get(targetIndex).getHP()){
            //if their HP's are same they both die
            this.board.deleteIndex(charPos[0],charPos[1]);
            this.chars.remove(charIndex);
            for(int i=0;i< this.chars.size();i++){
                if(this.chars.get(i).getId().equals(targetCharId)){
                    targetIndex=i;
                }
            }
            this.chars.remove(targetIndex);

        }else{
            int charHP= this.chars.get(charIndex).getHP();
            int charAP=this.chars.get(charIndex).getAP();
            int targetHP=this.chars.get(targetIndex).getHP();
            targetHP-=charAP;
            if(targetHP<this.chars.get(charIndex).getHP()){//then target dies
                if(targetHP>0){
                    this.chars.get(charIndex).setHP(charHP-targetHP);
                }
                this.chars.remove(targetIndex);
                this.board.set(targetPos[0],targetPos[1],charId);
                this.board.deleteIndex(charPos[0],charPos[1]);
            }else if (targetHP>this.chars.get(charIndex).getHP()){//then attacker dies
                if(this.chars.get(charIndex).getHP()>0){
                    this.chars.get(targetIndex).setHP(targetHP-charHP);
                }
                this.chars.remove(charIndex);
                this.board.deleteIndex(charPos[0],charPos[1]);
            }


        }


    }

    //returns 1 if two characters are enemy
    public Boolean isEnemy(String charId, String targetCharId) {
        int charIndex=0;
        int targetIndex=0;
        if(charId.equals(targetCharId)){
            return false;
        }
        for(int i=0;i< this.chars.size();i++){
            if(this.chars.get(i).getId().equals(charId)){
                charIndex=i;
            }else if(this.chars.get(i).getId().equals(targetCharId)){
                targetIndex=i;
            }
        }
        return Main.xor((this.chars.get(charIndex) instanceof Calliance),(this.chars.get(targetIndex) instanceof Calliance));
    }

    public boolean isMovementValid(Integer index, int[] moves){
        if(chars.get(index).getMaxMove()!=(moves.length/2)){
            return false;
        }else{
            return true;
        }
    }

    public void placeChars(ArrayList<String> charLines){
        chars= new ArrayList<>();
        for(String line: charLines){
            String[] lineArr= line.split(" ");
            switch (lineArr[0]){
                case "ELF":
                    chars.add((Character) new Elf(lineArr[1]));
                    break;
                case "DWARF":
                    chars.add((Character) new Dwarf(lineArr[1]));
                    break;
                case "HUMAN":
                    chars.add((Character) new Human(lineArr[1]));
                    break;
                case "GOBLIN":
                    chars.add((Character) new Goblin(lineArr[1]));
                    break;
                case "TROLL":
                    chars.add((Character) new Troll(lineArr[1]));
                    break;
                case "ORK":
                    chars.add((Character) new Ork(lineArr[1]));
                    break;
            }
            board.set(Integer.parseInt(lineArr[3]),Integer.parseInt(lineArr[2]),lineArr[1]);
        }
        //sort the characters by their id's
        Collections.sort(chars, new Comparator<Character>() {
            @Override
            public int compare(Character o1, Character o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
    }


}
