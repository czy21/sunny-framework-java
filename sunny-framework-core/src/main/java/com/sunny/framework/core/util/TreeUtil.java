package com.sunny.framework.core.util;


import com.sunny.framework.core.model.TreeNode;
import com.sunny.framework.core.model.TreeNodeOperation;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeUtil {

    public static <V, T extends TreeNode<V>> void assemble(List<T> all) {
        all.forEach(t -> t.setParentKeys(getParentIds(all, t)));
    }

    public static <V, T extends TreeNode<V>> List<V> getParentIds(List<T> items, T node) {
        return items.stream()
                .filter(t -> t.getKey().equals(node.getParentKey()))
                .flatMap(t -> Stream.concat(getParentIds(items, t).stream(), Stream.of(node.getParentKey())))
                .collect(Collectors.toList());
    }

    public static <V, T extends TreeNode<V>> List<T> buildByPath(Supplier<T> supplier, List<T> all) {
        return buildByPath(supplier, all, null);
    }

    public static <V, T extends TreeNode<V>> List<T> buildByPath(Supplier<T> supplier, List<T> all, Consumer<T> decoNodeFunc) {
        return buildByPath(supplier, all, decoNodeFunc, null);
    }

    @SuppressWarnings("unchecked")
    public static <V, T extends TreeNode<V>> List<T> buildByPath(Supplier<T> supplier, List<T> all, Consumer<T> decoNodeFunc, Comparator<T> sortComparator) {

        T root = supplier.get();

        for (T t : all) {
            if (t.getPathKeys() == null) {
                continue;
            }
            T current = root;
            for (int i = 0; i < t.getPathKeys().size(); i++) {
                current.setChildren(Optional.ofNullable(current.getChildren()).orElse(new ArrayList<>()));
                V p = t.getPathKeys().get(i);
                T child = (T) current.getChildren().stream().filter(c -> Objects.equals(p, c.getKey())).findFirst().orElse(null);
                if (child == null) {
                    child = supplier.get();
                    child.setKey(p);
                    child.setParentKey(current.getKey());
                    child.setParentKeys(t.getPathKeys().subList(0, i));
                    child.setLevel(i + 1);
                    ((List<T>) current.getChildren()).add(child);
                }
                if (decoNodeFunc != null) {
                    decoNodeFunc.accept(child);
                }
                if (sortComparator != null) {
                    ((List<T>) current.getChildren()).sort(sortComparator);
                }
                current = child;
            }
        }
        return (List<T>) root.getChildren();
    }

    public static <V, T extends TreeNode<V>> List<T> build(Supplier<T> supplier, List<T> all) {
        return build(supplier, all, null);
    }

    public static <V, T extends TreeNode<V>> List<T> build(Supplier<T> supplier, List<T> all, Consumer<T> decoNodeFunc) {
        return build(supplier, all, decoNodeFunc, null);
    }

    @SuppressWarnings("unchecked")
    public static <V, T extends TreeNode<V>> List<T> build(Supplier<T> supplier, List<T> all, Consumer<T> decoNodeFunc, Comparator<T> sortComparator) {

        T root = supplier.get();

        buildChildren(all, root, decoNodeFunc, sortComparator, 0);

        return (List<T>) Optional.ofNullable(root.getChildren()).orElse(new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public static <V, T extends TreeNode<V>> void buildChildren(List<T> all, T node, Consumer<T> decoNodeFunc, Comparator<T> sortComparator, int level) {

        if (decoNodeFunc != null) {
            decoNodeFunc.accept(node);
        }

        node.setLevel(level);

        for (T t : all) {
            if (Objects.equals(node.getKey(), t.getParentKey())) {
                buildChildren(all, t, decoNodeFunc, sortComparator, level + 1);
                node.setChildren(Optional.ofNullable(node.getChildren()).orElse(new ArrayList<>()));
                ((List<T>) node.getChildren()).add(t);
                if (sortComparator != null) {
                    ((List<T>) node.getChildren()).sort(sortComparator);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <V, T extends TreeNode<V>> List<T> filter(Supplier<T> supplier, List<T> tree, boolean keepChildren, Predicate<T> predicate, BiConsumer<T, T> decoNodeFunc) {
        if (tree == null) {
            return null;
        }
        List<T> filteredTree = new ArrayList<>();
        for (T t : tree) {
            boolean predicateResult = predicate.test(t);
            List<T> filteredChildren = keepChildren && predicateResult ? (List<T>) t.getChildren() : filter(supplier, (List<T>) t.getChildren(), keepChildren, predicate, decoNodeFunc);
            if (predicateResult || (filteredChildren != null && !filteredChildren.isEmpty())) {
                T node = supplier.get();
                node.setChildren(filteredChildren);
                decoNodeFunc.accept(t, node);
                filteredTree.add(node);
            }
        }
        return filteredTree;
    }

    public static <V, T extends TreeNode<V>> List<T> getOperationNodes(List<T> t1, Predicate<T> includePredicate, Consumer<T> updateConsumer, List<T> t2) {

        Set<V> includeNodeIds = t1.stream().filter(includePredicate).map(T::getKey).collect(Collectors.toSet());

        List<T> result = new ArrayList<>(t2);

        result.forEach(t -> {
            if (!includeNodeIds.contains(t.getKey())) {
                t.setOperation(TreeNodeOperation.DELETE);
            } else {
                t.setOperation(TreeNodeOperation.UPDATE);
                updateConsumer.accept(t);
            }
        });
        t1.stream()
                .filter(t -> includeNodeIds.contains(t.getKey()))
                .filter(t -> result.stream().noneMatch(n -> Objects.equals(n.getKey(), t.getKey())))
                .forEach(t -> {
                    t.setOperation(TreeNodeOperation.INSERT);
                    result.add(t);
                });
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <VS,VT, S extends TreeNode<VS>, T extends TreeNode<VT>> List<T> mapTo(List<S> sources, Function<S, T> stFunction) {
        return sources.stream()
                .map(s -> {
                    T target = stFunction.apply(s);
                    if (s.getChildren() != null && !s.getChildren().isEmpty()) {
                        target.setChildren(mapTo((List<S>) s.getChildren(), stFunction));
                    }
                    return target;
                }).toList();
    }
}
