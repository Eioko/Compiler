package backend;

public class MipsBuilder {
    private static final MipsBuilder mipsBuilder = new MipsBuilder();
    private MipsBuilder() {}
    public static MipsBuilder getInstance() {
        return mipsBuilder;
    }
}
