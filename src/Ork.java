import java.io.IOException;

public class Ork extends Zorde{
    private int healPoints;
    public Ork(String id) {
        super(id);
        this.setAP(Constants.orkAP);
        this.setHP(Constants.orkHP);
        this.setMaxMove(Constants.orkMaxMove);
        this.setMaxHP(this.getHP());
        this.healPoints=Constants.orkHealPoints;

    }

    public int getHealPoints() {
        return healPoints;
    }

    public void setHealPoints(int healPoints) {
        this.healPoints = healPoints;
    }

    public static void heal(Ork ork,Character targetChar) throws IOException {
        Gameplay gameplay= new Gameplay();
        if(!gameplay.isEnemy(ork.getId(),targetChar.getId())){
            int healAmount= ork.getHealPoints();
            int targetHP=targetChar.getHP();
            if((targetHP+healAmount)>targetChar.getMaxHP()){
                targetHP=targetChar.getMaxHP();
            }else{
                targetHP+=healAmount;
            }
            targetChar.setHP(targetHP);
        }
    }
}
