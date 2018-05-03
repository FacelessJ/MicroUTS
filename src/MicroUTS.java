import rts.*;
import ai.*;
import ai.core.AI;

public class MicroUTS {
    public static void main(String args[]) throws Exception {
        System.out.println("Hello, world!");
        AI randAI = new RandomBiasedAI();
        randAI.reset();
        System.out.println("AI instantiated and reset");
    }
}