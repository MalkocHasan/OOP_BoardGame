public class Elf extends Calliance{
    private int rangedAP;
    public Elf(String id){
        super(id);
        this.setAP(Constants.elfAP);
        this.setHP(Constants.elfHP);
        this.setMaxMove(Constants.elfMaxMove);
        this.rangedAP=Constants.elfRangedAP;
        this.setMaxHP(this.getHP());
    }

    public int getRangedAP() {
        return rangedAP;
    }

    public void setRangedAP(int rangedAP) {
        this.rangedAP = rangedAP;
    }
}
