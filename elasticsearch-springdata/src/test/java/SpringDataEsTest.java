import com.pci.es.entity.Article;
import com.pci.es.repositories.ArticleRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author zyting
 * @sinne 2020-09-25
 * springdata操作es
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringDataEsTest {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchTemplate template;

    /**
     * 创建索引
     * @throws Exception
     */
    @Test
    public void craeteIndex() throws Exception{
        // 创建索引，并配置映射关系
        template.createIndex(Article.class);
        // 只有索引库，需要配置映射关系
        // template.putMapping(Article.class);
    }

    /**
     * 向索引库中添加文档
     */
    @Test
    public void addDocument() throws Exception{
        for (int i=10;i<=20;i++){
            Article article = new Article();
            article.setId(i);
            article.setTitle("推动构建人类命运共同体"+i);
            article.setContent("世界和平不可分割，人类命运休戚与共。参加联合国维和行动30年来，中国军队以实实在在的行动让和平之光照亮世界，为发展营造和平稳定的环境，把温暖、爱心和友谊传递到四面八方。");
            articleRepository.save(article);
        }
    }

    /**
     * 根据id删除
     * @throws Exception
     */
    @Test
    public void deleteDocById() throws Exception {
        articleRepository.deleteById(1L);
        // 全部删除
        articleRepository.deleteAll();
    }

    /**
     * 修改是先将原来的删除再添加，因此只需要再添加一次 要更新的id的
     * @throws Exception
     */
    @Test
    public void updateDocument() throws Exception{
        Article article = new Article();
        article.setId(2);
        article.setTitle("数字经济将成为推动双循环的重要力量2");
        article.setContent("修改：疫情让我们看到了数字经济的强大优势，也让我们看到数字经济对打通国内大循环、拉动内需、促进消费的重要意义。");
        articleRepository.save(article);
    }

    /**
     * 查询所有
     */
    @Test
    public void findAll(){
        Iterable<Article> all = articleRepository.findAll();
        all.forEach(article -> System.out.println(article));
    }

    /**
     * 根据id 查询
     */
    @Test
    public void findById(){
        Optional<Article> optional = articleRepository.findById(1L);
        Article article = optional.get();
        System.out.println(article);
    }

    /**
     * 自定义方法，根据标题查询
     */
    @Test
    public void findByTitle(){
        List<Article> articles = articleRepository.findByTitle("数字");
        articles.stream().forEach(article -> System.out.println(article));
    }

    /**
     * title包含“数字”或者内容中包含“和平”的数据
     */
    @Test
    public void findByTitleOrContent(){
        articleRepository.findByTitleOrContent("数字", "和平")
                .forEach(article -> System.out.println(article));
    }

    /**
     * title包含“数字”或者内容中包含“和平”的数据
     */
    @Test
    public void findByTitleOrContentPage() throws Exception{
        Pageable pageable = PageRequest.of(2, 5);
        articleRepository.findByTitleOrContent("数字", "和平",pageable)
                .forEach(article -> System.out.println(article));
    }

    /**
     * 分词后只需要满足其中一个即可被搜索到
     * @throws Exception
     */
    @Test
    public void testNativeSearchQuery() throws Exception{
        //
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("数字与加点").defaultField("title"))
                .withPageable(PageRequest.of(0, 15))
                .build();
        List<Article> articles = template.queryForList(query, Article.class);
        articles.stream().forEach(s -> System.out.println(s));
    }

}
