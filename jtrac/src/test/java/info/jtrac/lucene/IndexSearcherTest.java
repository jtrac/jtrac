package info.jtrac.lucene;

import info.jtrac.domain.Item;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class IndexSearcherTest extends TestCase {
    
    private ApplicationContext context;
    
    @Override
    public void setUp() {
        System.setProperty("jtrac.home", "target/home");
        context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/applicationContext-lucene.xml");
    }
    
    public void testFindItemIdsBySearchingWithinSummaryAndDetailFields() {
        Item item = new Item();
        item.setId(1);
        item.setSummary("this is a test summary");
        item.setDetail("the quick brown fox jumped over the lazy dogs");
        Indexer indexer = (Indexer) context.getBean("indexer");
        indexer.index(item);
        IndexSearcher searcher = (IndexSearcher) context.getBean("indexSearcher");
        List list = searcher.findItemIdsContainingText("lazy");
        assertEquals(1, list.size());
        list = searcher.findItemIdsContainingText("foobar");
        assertEquals(0, list.size());
    }
    
}
