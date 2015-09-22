package jp.ac.tohoku.ecei.sb.gprdb.config;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.Loader;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Paths;

/**
 * @author Shu Tadaka
 */
public class ClasspathLoader implements Loader {

    @Setter
    private String charset = "UTF-8";

    @Setter
    private String prefix = "";

    @Setter
    private String suffix = ".html";

    @Override
    public Reader getReader(String templateName) throws LoaderException {
        try {
            String path = Paths.get(this.prefix, templateName + this.suffix).normalize().toAbsolutePath().toString();
            return new InputStreamReader(getClass().getResource(path).openStream());
        } catch (IOException ex) {
            throw new LoaderException(ex, "error");
        }
    }

}
