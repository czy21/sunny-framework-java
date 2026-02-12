package com.sunny.framework.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.framework.core.model.SimpleItemModel;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class TreeUtilTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void test1() throws Exception {
        URL url = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "tree.json");
        List<SimpleItemModel<String>> items = objectMapper.readValue(url, new TypeReference<List<SimpleItemModel<String>>>() {
        });
        List<SimpleItemModel<String>> tree = TreeUtil.build(SimpleItemModel::new, items,
                t -> {
                    t.setParentKeys(TreeUtil.getParentIds(items, t));
                },
                Comparator.comparing(SimpleItemModel::getSort)
        );

        List<SimpleItemModel<String>> filteredTree = TreeUtil.filter(SimpleItemModel::new, tree,false,
                t -> "a3-2".equals(t.getKey()),
                (item, node) -> {
                    node.setKey(item.getKey());
                    node.setParentKey(item.getParentKey());
                    node.setLabel(item.getLabel());
                    System.out.println();
                });
        System.out.println();
    }

    @Test
    public void test2() throws Exception {
        URL url = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "path.json");
        List<SimpleItemModel<String>> items = objectMapper.readValue(url, new TypeReference<List<SimpleItemModel<String>>>() {
        });
        List<SimpleItemModel<String>> tree = TreeUtil.buildByPath(SimpleItemModel::new, items, null, Comparator.comparing(SimpleItemModel::getSort)
        );
        System.out.println();
    }
}