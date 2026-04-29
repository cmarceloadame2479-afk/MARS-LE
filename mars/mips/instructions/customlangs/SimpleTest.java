package mars.mips.instructions.customlangs;

import mars.mips.instructions.*;

public class SimpleTest extends CustomAssembly {
    public SimpleTest() {
        super();
    }

    @Override
    public String getName() {
        return "Simple Test";
    }

    @Override
    public String getDescription() {
        return "Testing if loader works";
    }

    @Override
    protected void populate() {
        // Leave empty for now just to see if it loads
    }
}