 package mars.mips.instructions.customlangs;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;

public class TeamPlasmaAssembly extends CustomAssembly{
    @Override
    public String getName(){
        return "Team Plasma Assembly";
    }

    @Override
    public String getDescription(){
        return "Try to liberate as many pokemon as you can for the greater good of humanity!";
    }

    @Override
    protected void populate(){
        instructionList.add(
            new BasicInstruction("lib $t1, 1", 
            "Liberate: Change the amount of pokemon liberated ($t1) by and immediate value",
         BasicInstructionFormat.I_FORMAT,
        "00100 fffff 00000 ssssssssssssssss",
    new SimulationCode()
    {
        public void simulate(ProgramStatement statement) throws ProcessingException{
            int[] operands = statement.getOperands();
                     int add1 = RegisterFile.getValue(operands[0]);
                     int add2 = operands[1] << 16 >> 16;
                     int sum = add1 + add2;
                     RegisterFile.updateRegister(operands[0], sum);
        }
    }));
    }
}