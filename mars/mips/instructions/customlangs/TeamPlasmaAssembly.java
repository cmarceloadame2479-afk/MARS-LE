 package mars.mips.instructions.customlangs;
    import mars.mips.hardware.*;
    import mars.*;
    import mars.util.*;
    import mars.mips.instructions.*;
    import java.util.Random;

public class TeamPlasmaAssembly extends CustomAssembly{
    private static int trainer = -1;
    private static int success = 0;
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

    instructionList.add(
        new BasicInstruction("bat", "Attempt to liberate a Pokemon by challenging a trainer to a battle", BasicInstructionFormat.R_FORMAT, 
    "000000 00000 00000 00000 00000 000010", 
    new SimulationCode(){
        public void simulate(ProgramStatement statement) throws ProcessingException{
            Random rand = new Random();
            if (trainer == -1){
                SystemIO.printString("Go look for a Trainer!\n");
                return; 
            }
            else{
                //Simulate the difficulty of a battle based on the trainer
                // 90%(School Kid), 75%(Bug Catcher), 50%(Ace Trainer), 25%(Champion)
                switch (RegisterFile.getValue(2)){ // 2 = register $v0
                    case 0:
                        // 90%
                        if (rand.nextInt(100) < 90){
                            success = 1;
                            SystemIO.printString("You won!\n");
                            break;
                        } else {
                            SystemIO.printString("You lost!\n");
                            break;
                        }
                    case 1: 
                        // 75%
                        if(rand.nextInt(100) < 75){
                            success = 1;
                            SystemIO.printString("You won!\n");
                            break;
                        } else {
                            SystemIO.printString("You lost!\n");
                            break;
                        }
                    case 2:
                        // 50%
                        if (rand.nextInt(100) < 50){
                            success = 1;
                            SystemIO.printString("You won!\n");
                            break;
                        } else {
                            SystemIO.printString("You lost!\n");
                            break;
                        }
                    case 3:
                        // 25%
                        if (rand.nextInt(100) < 25){
                            success = 1;
                            SystemIO.printString("You won!\n");
                            break;
                        } else {
                            SystemIO.printString("You lost!\n");
                            break;
                        }
                }
            }
        }
    }));
    instructionList.add(
        new BasicInstruction("libsuc label",
            "Liberation Success: Jump to a label only if previous battle was won",
            BasicInstructionFormat.I_BRANCH_FORMAT, 
            "100000 00000 00000 ffffffffffffffff",
            new SimulationCode(){
                public void simulate(ProgramStatement statement) throws ProcessingException {
                    // Checks if battle was successful
                    if (success == 1){
                        String label = statement.getOriginalTokenList().get(1).getValue();
                        int targetAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);
                        if (targetAddress == -1){
                            throw new ProcessingException(statement, "Label not found.");
                        }
                        Globals.instructionSet.processJump(targetAddress);

                        success = 0;
                    }
                }
            }));
    instructionList.add(
        new BasicInstruction("jump label",
            "Jump: Simply jumps to label unconditionally",
            BasicInstructionFormat.I_BRANCH_FORMAT,
            "111000 00000 00000 ffffffffffffffff",
            new SimulationCode(){
                public void simulate(ProgramStatement statement) throws ProcessingException{
                     String label = statement.getOriginalTokenList().get(1).getValue();
                        int targetAddress = Globals.program.getLocalSymbolTable().getAddressLocalOrGlobal(label);
                        if (targetAddress == -1){
                            throw new ProcessingException(statement, "Label not found.");
                        }
                        Globals.instructionSet.processJump(targetAddress);
                }
            }));


    }
}