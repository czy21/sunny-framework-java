package com.sunny.framework.core.automap;

import java.util.List;

public interface BaseAutoMap<S, T> {

    T mapToTarget(S source);

    S mapToSource(T target);

    List<T> mapToTargets(List<S> sources);

    List<S> mapToSources(List<T> targets);


}
