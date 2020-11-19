package com.kyocoolcool.demo.service;

import com.alibaba.fastjson.JSON;
import com.kyocoolcool.demo.pojo.Content;
import com.kyocoolcool.demo.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris Chen https://blog.kyocoolcool.com
 * @version 1.0
 * @since 2020/11/17 7:39 PM
 **/
@Service
public class ContentService {
    private final RestHighLevelClient client;

    @Autowired
    public ContentService(RestHighLevelClient restHighLevelClient) {
        this.client = restHighLevelClient;
    }

    public Boolean parseContent(String keyWord) throws IOException {
        final HtmlParseUtil util = new HtmlParseUtil();
        final List<Content> contents = util.parseHtml(keyWord);
        final BulkRequest request = new BulkRequest();
        request.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            request.add(new IndexRequest("kyocoolcool_jd")
                    .source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        final BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        return !response.hasFailures();
    }

    public List<Map<String,Object>> searchPage(String keyWord,int pageNo,int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        final SearchRequest request = new SearchRequest("kyocoolcool_jd");
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        final TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyWord);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    public List<Map<String,Object>> searchHightPage(String keyWord,int pageNo,int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        final SearchRequest request = new SearchRequest("kyocoolcool_jd");
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        final TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyWord);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        final HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多個high light顯示
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits()) {
            //獲取high light 字串
            final Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            final HighlightField title = highlightFields.get("title");
            final Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            if (title != null) {
                final Text[] fragments = title.fragments();
                StringBuilder n_title = new StringBuilder();
                for (Text fragment : fragments) {
                    n_title.append(fragment);
                }
                sourceAsMap.put("title", n_title.toString());
            }
            list.add(sourceAsMap);
        }
        return list;
    }

    public List<Map<String,Object>> searchLikeHightPage(String keyWord,int pageNo,int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        final SearchRequest request = new SearchRequest("kyocoolcool_jd");
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        final MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyWord);
        sourceBuilder.query(matchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        final HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多個high light顯示
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);
        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits()) {
            //獲取high light 字串
            final Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            final HighlightField title = highlightFields.get("title");
            final Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            if (title != null) {
                final Text[] fragments = title.fragments();
                StringBuilder n_title = new StringBuilder();
                for (Text fragment : fragments) {
                    n_title.append(fragment);
                }
                sourceAsMap.put("title", n_title.toString());
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}
