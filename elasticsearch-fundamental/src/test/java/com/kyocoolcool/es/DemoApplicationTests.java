package com.kyocoolcool.es;

import com.alibaba.fastjson.JSON;
import com.kyocoolcool.es.entity.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.kyocoolcool.es.util.Const.ES_INDEX;

@SpringBootTest
class DemoApplicationTests {
    @Qualifier("restHighLevelClient")
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {

    }

    @Test
    public void testCreateIndex() throws IOException {
        final CreateIndexRequest request = new CreateIndexRequest("kyocoolcool_index");
        final CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void testDeleteIndex() throws IOException {
        final DeleteIndexRequest request = new DeleteIndexRequest("kyocoolcool_index");
        final AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    @Test
    public void testAddDocument() throws IOException {
        final User user = new User("chris", 30);
        final IndexRequest request = new IndexRequest("kyocoolcool_index");
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
//        request.timeout("1s");
        request.source(JSON.toJSONString(user), XContentType.JSON);
        final IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }

    @Test
    public void testIsExists() throws IOException {
        final GetRequest request = new GetRequest("kyocoolcool_index", "1");
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        final boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    @Test
    public void testGetDocument() throws IOException {
        final GetRequest request = new GetRequest("kyocoolcool_index", "1");
        final GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }

    @Test
    public void testUpdateDocument() throws IOException {
        final UpdateRequest request = new UpdateRequest("kyocoolcool_index", "1");
        request.timeout("1s");
        final User user = new User("God Chris", 20);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        final UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    @Test
    public void testDeleteRequest() throws IOException {
        final DeleteRequest request = new DeleteRequest("kyocoolcool_index", "1");
        request.timeout("1s");
        final DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    @Test
    public void testBulkRequest() throws IOException {
        final BulkRequest request = new BulkRequest();
        request.timeout("10s");
        final ArrayList<User> userLists = new ArrayList<>();
        userLists.add(new User("chris6", 6));
        userLists.add(new User("chris7", 7));
        userLists.add(new User("chris8", 8));
        userLists.add(new User("chris9", 9));
        userLists.add(new User("chris10", 10));

        for (int i = 0; i < userLists.size(); i++) {
            request.add(
                    new IndexRequest("kyocoolcool_index")
//                            .id("" + i + 1)
                            .source(JSON.toJSONString(userLists.get(i)), XContentType.JSON));
        }
        final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(response.hasFailures());
    }

    @Test
    public void testSearch() throws IOException {
        final SearchRequest request = new SearchRequest(ES_INDEX);
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        final TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "chris6");
//        final MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(response.getHits()));
        System.out.println("==============");
        for (SearchHit documentFields : response.getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
