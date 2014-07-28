package sortpom.util;

import org.apache.commons.io.IOUtils;
import sortpom.exception.FailureException;
import sortpom.parameter.PluginParameters;

import java.io.*;
import java.net.URL;

/**
 * Used to interface with file system
 *
 * @author Bjorn
 */
public class FileUtil {
    private static final String DEFAULT_SORT_ORDER_FILENAME = "default_1_0_0";
    private static final String XML_FILE_EXTENSION = ".xml";
    private final SortOrderFileStore sortOrderFileStore;
    private File pomFile;
    private String backupFileExtension;
    private String encoding;
    private String customSortOrderFile;
    private String predefinedSortOrder;
    private String newName;
    private File backupFile;

    public FileUtil(SortOrderFileStore sortOrderFileStore) {
        this.sortOrderFileStore = sortOrderFileStore;
    }

    /** Initializes the class with sortpom parameters. */
    public void setup(PluginParameters parameters) {
        this.pomFile = parameters.pomFile;
        this.backupFileExtension = parameters.backupFileExtension;
        this.encoding = parameters.encoding;
        this.customSortOrderFile = parameters.customSortOrderFile;
        this.predefinedSortOrder = parameters.predefinedSortOrder;
    }

    /**
     * Saves a backup of the pom file before sorting.
     */
    public void backupFile() {
        createFileHandle();
        checkBackupFileAccess();
        createBackupFile();
    }

    void createFileHandle() {
        newName = pomFile.getAbsolutePath() + backupFileExtension;
        backupFile = new File(newName);
    }

    private void checkBackupFileAccess() {
        if (backupFile.exists() && !backupFile.delete()) {
            throw new FailureException("Could not remove old backup file, filename: " + newName);
        }
    }

    private void createBackupFile() {
        FileInputStream source = null;
        FileOutputStream newFile = null;
        try {
            source = new FileInputStream(pomFile);
            newFile = new FileOutputStream(backupFile);
            IOUtils.copy(source, newFile);
        } catch (IOException e) {
            throw new FailureException("Could not create backup file to filename: " + newName, e);
        } finally {
            IOUtils.closeQuietly(newFile);
            IOUtils.closeQuietly(source);
        }
    }

    /**
     * Loads the pom file that will be sorted.
     *
     * @return Content of the file
     */
    public String getPomFileContent() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(pomFile);
            return IOUtils.toString(inputStream, encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new FailureException("Could not handle encoding: " + encoding, ex);
        } catch (IOException ex) {
            throw new FailureException("Could not read pom file: " + pomFile.getAbsolutePath(), ex);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Saves sorted pom file.
     *
     * @param sortedXml The content to save
     */
    public void savePomFile(final String sortedXml) {
        FileOutputStream saveFile = null;
        try {
            saveFile = new FileOutputStream(pomFile);
            IOUtils.write(sortedXml, saveFile, encoding);
        } catch (IOException e) {
            throw new FailureException("Could not save sorted pom file: " + pomFile.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(saveFile);
        }
    }

    public byte[] getDefaultSortOrderXmlBytes() throws IOException {
        return getDefaultSortOrderXml().getBytes(encoding);
    }

    /**
     * Retrieves the default sort order for sortpom
     *
     * @return Content of the default sort order file
     */
    private String getDefaultSortOrderXml() throws IOException {
        InputStream inputStream = null;
        try {
            if (customSortOrderFile != null) {
                UrlWrapper urlWrapper = new UrlWrapper(customSortOrderFile);
                if (urlWrapper.isUrl()) {
                    inputStream = urlWrapper.openStream();
                } else {
                    inputStream = openCustomSortOrderFile();
                }
            } else if (predefinedSortOrder != null) {
                inputStream = getPredefinedSortOrder(predefinedSortOrder);
            } else {
                inputStream = getPredefinedSortOrder(DEFAULT_SORT_ORDER_FILENAME);
            }
            
            String sortOrderFile = IOUtils.toString(inputStream, encoding);
            sortOrderFileStore.setContent(sortOrderFile, encoding);
            
            return sortOrderFile;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Load custom sort order file from absolute or class path. If that fails, then try to retrieve it from a previous 
     * invocation of the plugin.
     * 
     * @return
     * @throws IOException
     */
    private InputStream openCustomSortOrderFile() throws IOException {
        FileLoader fileLoader = new FileLoader(customSortOrderFile);

        fileLoader.openAbsoluteFilePath();
        if (fileLoader.isFound()) {
            return fileLoader.getInputStream();
        }

        fileLoader.openFileFromClassPath();
        if (fileLoader.isFound()) {
            return fileLoader.getInputStream();
        }

        if (sortOrderFileStore.containsContent()) {
            return sortOrderFileStore.getInputStream();
        }

        throw new FileNotFoundException(String.format("Could not find %s or %s in classpath", new File(
                customSortOrderFile).getAbsolutePath(), customSortOrderFile));
    }

    private InputStream getPredefinedSortOrder(String predefinedSortOrder) throws IOException {
        URL resource = this.getClass().getClassLoader().getResource(predefinedSortOrder + XML_FILE_EXTENSION);
        if (resource == null) {
            throw new IllegalArgumentException(String.format("Cannot find %s among the predefined plugin resources",
                    predefinedSortOrder + XML_FILE_EXTENSION));
        }
        return resource.openStream();
    }

}
