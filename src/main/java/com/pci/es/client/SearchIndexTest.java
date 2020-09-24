package com.pci.es.client;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author zyting
 * @sinne 2020-09-24
 */
public class SearchIndexTest {

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
     * 根据id查询文档
     * @throws Exception
     */
    @Test
    public void searchById() throws Exception {

        //创建client对象
        // 创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.idsQuery()
                .addIds("1","2");
        search(queryBuilder);
    }

    /**
     * 根据关键字查询，term
     * @throws Exception
     */
    @Test
    public void searchByTerm() throws Exception{

        //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.termQuery("title", "努力");
        search(queryBuilder);
    }

    /**
     * 通过 querystring 查询
     * @throws Exception
     */
    @Test
    public void searchByQuerystring() throws Exception {
        //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("民族")
                .defaultField("title");
        search(queryBuilder,"title");
    }

    /**
     * 查询操作
     * @param queryBuilder
     * @throws Exception
     */
    private void search(QueryBuilder queryBuilder) throws Exception {
        // 执行查询
        SearchResponse response = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                // 设置开始位置
                .setFrom(0)
                // 设置每页显示的条数，查询多少条
                .setSize(5)
                .get();

        // 获取查询结果
        SearchHits hits = response.getHits();
        //取查询记录数
        long total = hits.getTotalHits();
        System.out.println("总记录数："+total);
        // 查询结果列表
        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，json格式输出
            System.out.println(searchHit.getSourceAsString());
            //获取文档属性
            Map<String, Object> document = searchHit.getSourceAsMap();
            System.out.println(document.get("id")+"  "+document.get("title")+"  "+document.get("content"));
        }

        // 关闭client
        client.close();
    }


    /**
     * 查询操作（分页和高亮）
     * @param queryBuilder
     * @throws Exception
     */
    private void search(QueryBuilder queryBuilder,String hightField) throws Exception {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(hightField);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        // 执行查询
        SearchResponse response = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                // 设置开始位置
                .setFrom(0)
                // 设置每页显示的条数，查询多少条
                .setSize(5)
                //设置高亮信息
                .highlighter(highlightBuilder)
                .get();

        // 获取查询结果
        SearchHits hits = response.getHits();
        //取查询记录数
        long total = hits.getTotalHits();
        System.out.println("总记录数："+total);
        // 查询结果列表
        Iterator<SearchHit> iterator = hits.iterator();
        while(iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，json格式输出
            System.out.println(searchHit.getSourceAsString());
            //获取文档属性
            Map<String, Object> document = searchHit.getSourceAsMap();
            System.out.println(document.get("id")+"  "+document.get("title")+"  "+document.get("content"));
            System.out.println("====== 高亮结果 =====");
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            //取高亮显示的结果
            HighlightField field = highlightFields.get(hightField);
            Text[] fragments = field.getFragments();
            if(fragments != null){
                String title = fragments[0].toString();
                System.out.println(title);
            }
        }

        // 关闭client
        client.close();
    }




}
