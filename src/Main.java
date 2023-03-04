import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static boolean xor(boolean a, boolean b) {
        return a != b;
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
	    Gameplay gameplay = new Gameplay(args[0],args[1],args[2]);
        gameplay.launch();
        gameplay.start();
    }
}
