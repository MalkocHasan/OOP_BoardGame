public class Goblin extends Zorde {
    public Goblin(String id){
        super(id);
        this.setAP(Constants.goblinAP);
        this.setHP(Constants.goblinHP);
        this.setMaxMove(Constants.goblinMaxMove);
        this.setMaxHP(this.getHP());
    }
}
