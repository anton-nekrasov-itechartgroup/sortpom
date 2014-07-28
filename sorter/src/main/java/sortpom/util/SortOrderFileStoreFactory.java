package sortpom.util;

/**
 * Creates a SortOrderFileStore or retrieves a static instance.
 * @author bjorn
 * @since 2014-07-28
 */
public class SortOrderFileStoreFactory {
    private static volatile SortOrderFileStore sortOrderFileStore;
    
    public synchronized SortOrderFileStore getStaticInstance() {
        if (sortOrderFileStore == null) {
            sortOrderFileStore = new SortOrderFileStore();
        }
        return sortOrderFileStore;
    }
    
    public synchronized SortOrderFileStore getNewInstance() {
        return new SortOrderFileStore();
    }
    
}
