package sortpom;

import sortpom.processinstruction.XmlProcessingInstructionParser;
import sortpom.util.FileUtil;
import sortpom.util.SortOrderFileStore;
import sortpom.util.SortOrderFileStoreFactory;
import sortpom.wrapper.WrapperFactoryImpl;

/**
 * Instantiates components and sets up component dependencies.
 * 
 * @author bjorn
 * @since 2014-07-28
 */
public class SortPomDependencyInjector {
    private final SortOrderFileStoreFactory sortOrderFileStoreFactory = new SortOrderFileStoreFactory();
    
    public SortPomImpl createSortPomImpl() {
        SortOrderFileStore sortOrderFileStore = sortOrderFileStoreFactory.getStaticInstance();
        
        FileUtil fileUtil = new FileUtil(sortOrderFileStore);
        WrapperFactoryImpl wrapperFactory = new WrapperFactoryImpl(fileUtil);
        XmlProcessor xmlProcessor = new XmlProcessor(wrapperFactory);
        XmlProcessingInstructionParser xmlProcessingInstructionParser = new XmlProcessingInstructionParser();
        
        return new SortPomImpl(fileUtil, xmlProcessor, wrapperFactory, xmlProcessingInstructionParser);
    }
    
    public SortPomImpl createNewTestInstance() {
        SortOrderFileStore sortOrderFileStore = sortOrderFileStoreFactory.getNewInstance();
        
        FileUtil fileUtil = new FileUtil(sortOrderFileStore);
        WrapperFactoryImpl wrapperFactory = new WrapperFactoryImpl(fileUtil);
        XmlProcessor xmlProcessor = new XmlProcessor(wrapperFactory);
        XmlProcessingInstructionParser xmlProcessingInstructionParser = new XmlProcessingInstructionParser();
        
        return new SortPomImpl(fileUtil, xmlProcessor, wrapperFactory, xmlProcessingInstructionParser);        
    }
}
