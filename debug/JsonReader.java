package template.debug;

import java.io.InputStream;
import java.util.PrimitiveIterator;

/**
 * @author dy[jealousing@gmail.com] on 17-5-9.
 */
public class JsonReader {
    private final InputStream inputStream;

    public JsonReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String[] readStringArray() {
        throw new UnsupportedOperationException();
    }
}
