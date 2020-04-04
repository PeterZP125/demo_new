package com.peter.mall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsSearchSkuInfo;
import com.peter.mall.beans.PmsSkuAttrValue;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.service.SkuService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;
    @Autowired
    JestClient jestClient;

    @Test
    void jestSearch() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.attrId", "40");
        boolQueryBuilder.filter(termQueryBuilder);
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", "RedmiK30");
        boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);

        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex("mall_sku").addType("PmsSearchSkuInfo").build();

        System.out.println(searchSourceBuilder.toString());

        SearchResult execute = jestClient.execute(search);

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);

        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            System.out.println("source-----------"+source);
            pmsSearchSkuInfos.add(source);
        }
        System.out.println(pmsSearchSkuInfos.size());
    }

    @Test
    void jestDSLAPI(){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //query
            //bool
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                //filter
                //term
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("a","aa");
                boolQueryBuilder.filter(termQueryBuilder);
                //terms
                TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("nums", new int[]{1, 2, 3});
                boolQueryBuilder.filter(termsQueryBuilder);
                //must
                //match
                MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name","Peter");
                boolQueryBuilder.must(matchQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        //from
        searchSourceBuilder.from(1);
        //size
        searchSourceBuilder.size(11);
        //highlight
        searchSourceBuilder.highlight();
        String dsl = searchSourceBuilder.toString();
        System.out.println(dsl);
    }

    @Test
    void storePmsSkuInfoIntoES() throws IOException {
        List<PmsSkuInfo> pmsSkuInfos = skuService.getAllPmsSkuInfo();
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.valueOf(pmsSkuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            Index index = new Index.Builder(pmsSearchSkuInfo).index("mall_sku").type("PmsSearchSkuInfo").id(pmsSearchSkuInfo.getId()+"").build();
            jestClient.execute(index);
        }
    }

}
