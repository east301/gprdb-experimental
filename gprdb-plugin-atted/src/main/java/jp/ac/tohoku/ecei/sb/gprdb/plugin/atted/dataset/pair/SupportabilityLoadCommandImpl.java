package jp.ac.tohoku.ecei.sb.gprdb.plugin.atted.dataset.pair;

import jp.ac.tohoku.ecei.sb.gprdb.app.CliCommand;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStore;
import jp.ac.tohoku.ecei.sb.gprdb.io.KeyValueStoreManager;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Optional;

/**
 * @author Shu Tadaka
 */
@Component
class SupportabilityLoadCommandImpl implements CliCommand {

    private static final String COMMAND = "atted-load-supportability";

    @Override
    public Optional<String> register(@NotNull Subparsers subparsers) {
        //
        Subparser parser = subparsers.addParser(COMMAND);
        parser.addArgument("source");

        return Optional.of(COMMAND);
    }

    @Override
    public void execute(@NotNull Namespace arguments, @NotNull ApplicationContext applicationContext)
        throws IOException, ParseException {

        //
        try (KeyValueStore<String, Integer> kvs = getMap(applicationContext)) {
            //
            kvs.clear();

            //
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(arguments.getString("source")))) {
                String line;
                int index = 0;

                while ((line = reader.readLine()) != null) {
                    //
                    index++;

                    String[] cols = line.split("\t");
                    if (cols.length != 3) {
                        throw new ParseException(arguments.getString("source"), index);
                    }

                    //
                    kvs.put("ncbigene:" + cols[0], Integer.parseInt(cols[1]));
                }
            }

            //
            kvs.commit();
            kvs.optimize();
        }
    }

    private static KeyValueStore<String, Integer> getMap(@NotNull ApplicationContext applicationContext) {
        return applicationContext
            .getBean(KeyValueStoreManager.class)
            .getMap("atted-supportability", String.class, Integer.class);
    }

}
