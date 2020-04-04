package com.peter.mall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.PmsSearchSkuInfo;
import com.peter.mall.beans.PmsSkuAttrValue;
import com.peter.mall.query.SkuInfoQueryParam;
import com.peter.mall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> getPmsSearchSkuInfoByParam(SkuInfoQueryParam skuInfoQueryParam) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        String dslStr = getQueryDSLString(skuInfoQueryParam);
        Search search = new Search.Builder(dslStr).addType("mall_sku").addType("PmsSearchSkuInfo").build();

        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);
            for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
                Map<String, List<String>> highlight = hit.highlight;
                if(highlight != null) {
                    pmsSearchSkuInfo.setSkuName(highlight.get("skuName").get(0));
                }
                pmsSearchSkuInfos.add(pmsSearchSkuInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pmsSearchSkuInfos;
    }

    private String getQueryDSLString(SkuInfoQueryParam skuInfoQueryParam){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[] valueIds = skuInfoQueryParam.getValueId();
        if(valueIds != null){
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.attrId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
        String catalog3Id = skuInfoQueryParam.getCatalog3Id();
        if(StringUtils.isNotBlank(catalog3Id)){
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        String keyword = skuInfoQueryParam.getKeyword();
        if(StringUtils.isNotBlank(keyword)){
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);
        //排序
        searchSourceBuilder.sort("id", SortOrder.DESC);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field("skuName");
        searchSourceBuilder.highlighter(highlightBuilder);
        return searchSourceBuilder.toString();
    }
}
