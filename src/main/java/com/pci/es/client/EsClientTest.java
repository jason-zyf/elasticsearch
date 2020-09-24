package com.pci.es.client;

import com.alibaba.fastjson.JSON;
import com.pci.es.bean.Article;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zyting
 * @sinne 2020-09-24
 * java客户端操作es
 */
public class EsClientTest {

    private TransportClient client;

    @Before
    public void init() throws UnknownHostException {
        // 1、创建一个settings对象
        Settings settings = Settings.builder()
                .put("cluster.name", "my-esCluster")
                .build();

        // 2、创建一个 TransportClient 对象
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9301))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9302))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9303));
    }

    /**
     * 创建索引库
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        //1.创建settings对象，配置信息
        Settings settings = Settings.builder().put("cluster.name","my-esCluster").build();
        //2.创建客户端
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9301));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9302));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9303));
        client.admin().indices().prepareCreate("index_hello").get();
        client.close();
    }

    /**
     * 设置 mapping 映射
     * @throws Exception
     */
    @Test
    public void setMappings() throws Exception {
        // 1、创建一个settings对象
        Settings settings = Settings.builder()
                .put("cluster.name", "my-esCluster")
                .build();

        // 2、创建一个 TransportClient 对象
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9301))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9302))
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9303));

        // 3、创建mapping信息
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("article")
                .startObject("properties")
                .startObject("id")
                .field("type", "long")
                .field("store", true)
                .endObject()
                .startObject("title")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .endObject()
                .startObject("content")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        // 使用 client把mapping信息设置到索引库中
        client.admin().indices()
                // 设置要做映射的索引
                .preparePutMapping("index_hello")
                // 社会要做映射的type
                .setType("article")
                // 设置mapping信息，可以是XContentBuilder对象也可以是json格式的字符串
                .setSource(builder)
                // 执行操作
                .get();
        // 关闭连接
        client.close();
    }

    /**
     * 添加文档到索引库
     * 方式一：通过 XContentBuilder 拼接json字符串
     */
    @Test
    public void addDocumentTest() throws Exception {

        // 创建一个client对象，已经创建好了

        String title = "认清巧取豪夺的实质";
        String content = "字节跳动与美国企业就TikTok的谈判进程可谓一波三折，美国政府先是声称已经“初步同意”协议方案，后又强调美国公司必须完全掌控TikTok，否则将不会批准协议。这场谈判，因为美国政府反复释放不同表态而不断“反转”。笔者认为，无论结果如何，有些事实是十分清楚的。";

        // 创建一个文档对象
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .field("id", 2L)
                    .field("title", title)
                    .field("content", content)
                .endObject();

        //把文档对象添加到索引库中
        client.prepareIndex()
                .setIndex("index_hello")
                .setType("article")
                .setId("2")
                .setSource(builder)
                .get();

        // 关闭连接
        client.close();
    }

    /**
     * 往索引库中添加文档
     * 方式二：采用json的方式
     * @throws Exception
     */
    @Test
    public void addDocument() throws Exception {

        // 创建 article对象,并设置对象的属性
        Article article = new Article();
        article.setId(3);
        article.setTitle("努力培养担当民族复兴大任的时代新人");
        article.setContent("教育、文化、卫生、体育事业与人民生活息息相关，人民对美好生活的向往就是我们党的奋斗目标。");

        // 把article对象转换成json格式字符串
        String str = JSON.toJSONString(article);
        // 使用client将文档写入索引库

        client.prepareIndex("index_hello", "article", "3")
                .setSource(str, XContentType.JSON)
                .get();

        // 关闭client
        client.close();
    }

    /**
     * 批量增加
     * @throws Exception
     */
    @Test
    public void muliAddDocument() throws Exception {

        for (int i = 4; i < 100 ; i ++){
            // 创建 article对象,并设置对象的属性
            Article article = new Article();
            article.setId(i);
            article.setTitle("努力培养担当民族复兴大任的时代新人"+i);
            article.setContent("教育、文化、卫生、体育事业与人民生活息息相关，人民对美好生活的向往就是我们党的奋斗目标。");

            // 把article对象转换成json格式字符串
            String str = JSON.toJSONString(article);
            // 使用client将文档写入索引库

            client.prepareIndex("index_hello", "article", i+"")
                    .setSource(str, XContentType.JSON)
                    .get();
        }

        // 关闭client
        client.close();
    }

}
