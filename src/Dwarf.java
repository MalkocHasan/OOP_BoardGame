public class Dwarf extends Calliance{
    public Dwarf(String id){
        super(id);
        this.setAP(Constants.dwarfAP);
        this.setHP(Constants.dwarfHP);
        this.setMaxMove(Constants.dwarfMaxMove);
        this.setMaxHP(this.getHP());
    }
}
