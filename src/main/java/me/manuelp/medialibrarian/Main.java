package me.manuelp.medialibrarian;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.manuelp.medialibrarian.data.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) throws IOException,
      InterruptedException {
    Configuration conf = readConfiguration(args);

    System.out.println("----[ MediaLibrarian welcomes you ]----");
    System.out.println("Configuration: " + conf.toString());

    new MediaLibrarian(conf).process();
  }

  private static Configuration readConfiguration(String[] args) {
    OptionParser parser = new OptionParser();
    OptionSpec<String> fromOption = parser
        .accepts("from", "Path of the root source directory tree.")
        .withRequiredArg().required().ofType(String.class);
    OptionSpec<String> toOption = parser
        .accepts("to", "Path of the archive directory.").withRequiredArg()
        .required().ofType(String.class);
    OptionSpec<Void> help = parser.accepts("help").forHelp();
    OptionSet opts = parser.parse(args);
    if (opts.has(help)) {
      try {
        parser.printHelpOn(System.out);
        System.exit(0);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return new Configuration(Paths.get(fromOption.value(opts)),
        Paths.get(toOption.value(opts)));
  }
}
