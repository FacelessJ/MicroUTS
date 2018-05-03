import rts.*;
import ai.*;
import ai.core.AI;
import rts.units.UnitTypeTable;
import standard.StrategyTactics;
import ai.pvai.PVAIML_ED;

public class MicroUTS {
    static UnitTypeTable utt = new UnitTypeTable(UnitTypeTable.VERSION_ORIGINAL_FINETUNED, UnitTypeTable.MOVE_CONFLICT_RESOLUTION_CANCEL_BOTH);
    public static void main(String args[]) throws Exception {
        System.out.println("Hello, world!");
        AI randAI = new RandomBiasedAI();
        randAI.reset();
        System.out.println("AI instantiated and reset");

        AI stratAI = new StrategyTactics(utt);
        stratAI.reset();
        System.out.println("Created a third party AI");

        AI pvaiML_ED = new PVAIML_ED(utt);
        pvaiML_ED.reset();
        System.out.println("Created another third party AI");
    }
}