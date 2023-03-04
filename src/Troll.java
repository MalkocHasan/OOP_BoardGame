public class Troll extends Zorde{
    public Troll(String id){
        super(id);
        this.setAP(Constants.trollAP);
        this.setHP(Constants.trollHP);
        this.setMaxMove(Constants.trollMaxMove);
        this.setMaxHP(this.getHP());
    }
}
