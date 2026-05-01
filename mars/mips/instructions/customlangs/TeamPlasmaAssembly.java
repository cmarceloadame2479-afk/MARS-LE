 package mars.mips.instructions.customlangs;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class TeamPlasmaAssembly extends CustomAssembly{
    private static int trainer = -1;
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

        instructionList.add(
            new BasicInstruction("enc", "Encounter a trainer to liberate their pokemon: store result in $v0", 
            BasicInstructionFormat.R_FORMAT, 
        "000000 00000 00000 00000 00000 000001", 
    new SimulationCode() {
        public void simulate(ProgramStatement statement) throws ProcessingException {
            // Possible trainers:  School Kid(0), Bug Catcher(1), Ace Trainer(2), Champion(3)

            // Get a random number
            Random rand = new Random();
            int num = rand.nextInt(4);
            trainer = num;
            RegisterFile.updateRegister(2, num);

            // Print the trainer encounter depending on the num
            switch (RegisterFile.getValue(2)){ // 2 = register $v0
                case 0:
                    SystemIO.printString("You encountered a School Kid!\n");
                    break;
                case 1:
                    SystemIO.printString("You encountered a Bug Catcher!\n");
                    break;
                case 2:
                    SystemIO.printString("You encountered an Ace Trainer!\n");
                    break;
                case 3:
                    SystemIO.printString("You encountered the Champion!\n");
                    break;
                default:
                    SystemIO.printString("Look for a Trainer!\n");


            }
        }
    }));

    }
}