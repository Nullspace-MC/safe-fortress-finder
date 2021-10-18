package net.nullspace_mc.safe_fortress_finder;

import java.io.File;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

// searches world seeds and 'Fortress.dat' files for fortresses with reliable nether brick spawning
@Command(name = "safe-fortress-finder", version = "1.0", mixinStandardHelpOptions = true)
public class Main implements Runnable {
    
    @Spec
    CommandSpec spec;

    @Option(names = {"-f", "--file"}, description = "World's Fortress.dat file.")
    File fortFile = null;

    @ArgGroup(exclusive = false)
    SeedArgs seedArgs;

    static class SeedArgs {

        @Option(names = {"-s", "--seed"}, description = "World seed.")
        Long seed = null;

        @Option(names = {"-r", "--range"}, description = "Range (in chunks) to search in the given seed.")
        Integer range = null;
    }

    public void run() {
        // print usage if neither seed nor file are specified
        if(seedArgs == null && fortFile == null) {
            CommandLine.usage(this, System.out);
            return;
        }
        FortressFinder ff = new FortressFinder();
        if(seedArgs != null) {
            // validate range
            if(seedArgs.range.intValue() < 0) {
                throw new ParameterException(spec.commandLine(),
                    String.format("Invalid value '%s' for option '--range': value is negative.",
                        seedArgs.range.intValue()
                    )
                );
            }
            // find fortresses in seed
            ff.searchSeed(seedArgs.seed, seedArgs.range);
        }
        if(fortFile != null) {
            // find fortresses in file
            //ff.searchFile(fortFile);
        }
        
        ff.cacheTable();
        ff.printCandidates(seedArgs == null && fortFile != null);
        if(seedArgs != null) ff.printMinTableSize();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}
