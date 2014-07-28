package sortpom.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Loads a file from either classpath or absolute path
 * 
 * @author bjorn
 * @since 2014-07-23
 */
class FileLoader {
    private final String fileName;
    private InputStream inputStream;

    /**
     * Creates a new instance of the class
     *
     */
    public FileLoader(String fileName) {
        this.fileName = fileName;
    }

    /** Try to open a stream to the file location */
    public void openAbsoluteFilePath() {
        try {
            this.inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException fex) {
            inputStream = null;
        }
    }

    /** Try to open a stream to the file location in the class path */
    public void openFileFromClassPath() {
        try {
            URL resource = this.getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                this.inputStream = resource.openStream();
            }
        } catch (IOException iex) {
            inputStream = null;
        }
    }

    /** Return true if stream is opened to file path */
    public boolean isFound() {
        return inputStream != null;
    }

    /** Return stream to file path content */
    public InputStream getInputStream() {
        return inputStream;
    }

}
