package com.sunny.framework.core.util;

import tools.jackson.core.type.TypeReference;
import com.sunny.framework.core.model.SimpleItemModel;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import tools.jackson.databind.json.JsonMapper;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TreeUtilTest {

    JsonMapper jsonMapper = new JsonMapper();

    @Test
    public void test1() throws Exception {
        URL url = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "tree.json");
        List<SimpleItemModel<String>> items = jsonMapper.readValue(url.openStream(), new TypeReference<List<SimpleItemModel<String>>>() {
        });
        List<SimpleItemModel<String>> tree = TreeUtil.build(SimpleItemModel::new, items,
                t -> {
                    t.setParentKeys(TreeUtil.getParentIds(items, t));
                },
                Comparator.comparing(item ->
                        Optional.ofNullable(item.getSort()).orElse(0)
                )
        );

        List<SimpleItemModel<String>> filteredTree = TreeUtil.filter(SimpleItemModel::new, tree,false,
                t -> "a3-2".equals(t.getKey()),
                (item, node) -> {
                    node.setKey(item.getKey());
                    node.setParentKey(item.getParentKey());
                    node.setLabel(item.getLabel());
                });
    }

    @Test
    public void test2() throws Exception {
        URL url = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + "path.json");
        List<SimpleItemModel<String>> items = jsonMapper.readValue(url.openStream(), new TypeReference<List<SimpleItemModel<String>>>() {
        });
        List<SimpleItemModel<String>> tree = TreeUtil.buildByPath(SimpleItemModel::new, items, null, Comparator.comparing(SimpleItemModel::getSort)
        );
        System.out.println();
    }
}