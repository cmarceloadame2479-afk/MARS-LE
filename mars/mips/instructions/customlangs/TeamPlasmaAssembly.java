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
        "001000 fffff 00000 ssssssssssssssss",
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

    instructionList.add(
        new BasicInstruction("tra label",
        "Transmission: An unknown entitiy transmits a message stored at the label",
        BasicInstructionFormat.I_BRANCH_FORMAT, 
        "110000 00000 00000 ffffffffffffffff",
        new SimulationCode(){
            public void simulate(ProgramStatement statement) throws ProcessingException{
                char ch = 0;
                     // Get the name of the label from the token list
                     String label = statement.getOriginalTokenList().get(1).getValue();
                     // Look up the label in the program symbol table to get its address
                     int byteAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);

                     try
                        {
                           ch = (char) Globals.memory.getByte(byteAddress);
                                             // won't stop until NULL byte reached!
                           while (ch != 0)
                           {
                              SystemIO.printString(new Character(ch).toString());
                              byteAddress++;
                              ch = (char) Globals.memory.getByte(byteAddress);
                           }
                        } 
                           catch (AddressErrorException e)
                           {
                              throw new ProcessingException(statement, e);
                           }
            }
        }));
    }
}