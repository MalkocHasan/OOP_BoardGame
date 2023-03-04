public class Human extends Calliance{
    public Human(String id){
        super(id);
        this.setAP(Constants.humanAP);
        this.setHP(Constants.humanHP);
        this.setMaxMove(Constants.humanMaxMove);
        this.setMaxHP(this.getHP());
    }
}
