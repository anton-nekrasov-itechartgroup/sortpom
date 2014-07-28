package sortpom.util;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * If the top-most maven module has loaded a custom configuration, then that configuration can be reused for sub modules.
 * This will make it easier an user to use one custom sort order file in the top module. The SortOrderFileStore must be
 * a static instance for this to work.
 *
 * @author bjorn
 * @since 2014-07-23
 */
public class SortOrderFileStore {
    private String content;
    private String encoding;

    /** Creates a new instance */
    SortOrderFileStore() {}
    
    /** Stores a sort order file */
    public void setContent(String content, String encoding) {
        this.content = content;
        this.encoding = encoding;
    }

    /** Returns true if content has been stored */
    public boolean containsContent() {
        return content != null;
    }

    /** Return the content as an input stream */
    public InputStream getInputStream() throws IOException {
        return IOUtils.toInputStream(content, encoding);
    }
}
