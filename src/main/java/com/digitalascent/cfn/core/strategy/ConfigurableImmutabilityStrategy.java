package com.digitalascent.cfn.core.strategy;

import com.digitalascent.base.core.SimpleApplicationObject;
import com.digitalascent.cfn.core.domain.CfnObject;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import groovy.lang.Tuple;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

public class ConfigurableImmutabilityStrategy extends SimpleApplicationObject implements ImmutabilityStrategy {
    public ConfigurableImmutabilityStrategy(List<String> semiMutablePropertyPaths, List<String> mutablePropertyPaths) {
        this.semiMutablePropertyPaths = ImmutableSet.copyOf(semiMutablePropertyPaths);
        this.mutablePropertyPaths = ImmutableSet.copyOf(mutablePropertyPaths);
    }

    @Override
    public Tuple maybeMakeImmutable(final String propertyPath, Object propertyValue) {
        if (!isImmutableProperty(propertyPath)) {
            getLogger().debug("Mutable property '{}'", propertyPath);
            return null;
        }

        if (semiMutablePropertyPaths.contains(propertyPath)) {
            getLogger().debug("Semi-mutable property '{}'", propertyPath);
            return new Tuple(new Object[]{makeSemiMutable(propertyValue)});
        }

        getLogger().debug("Immutable property '{}'", propertyPath);
        return new Tuple(new Object[]{makeImmutable(propertyValue)});
    }

    @SuppressWarnings("unchecked")
    private Object makeImmutable(Object o) {
        if (o instanceof String || o instanceof Number || o instanceof Boolean) {
            return o;
        }

        if (o instanceof CfnObject) {
            ((CfnObject) o).makeImmutable(this);
        }

        // TODO - handle arrays (normalize to list first)

        if (o instanceof List) {
            return ImmutableList.copyOf(makeListElementsImmutable((List) o));
        }

        if (o instanceof Set) {
            return ImmutableSet.copyOf(makeSetElementsImmutable((Set) o));
        }

        if (o instanceof Map) {
            return ImmutableMap.copyOf(makeMapEntriesImmutable((Map) o));
        }

        return o;
    }

    private Map<Object, Object> makeMapEntriesImmutable(Map<Object, Object> propertyMap) {
        final Map<Object, Object> map = new LinkedHashMap<>();

        propertyMap.forEach((key, value) -> map.put( makeImmutable(key), makeImmutable(value)));

        return map;
    }

    private Set<Object> makeSetElementsImmutable(Set<Object> originalSet) {
        return originalSet.stream().map(this::makeImmutable).collect(toImmutableSet());
    }

    private List<Object> makeListElementsImmutable(List<Object> originalList) {
        return originalList.stream().map(this::makeImmutable).collect(toImmutableList());
    }

    @SuppressWarnings("unchecked")
    private Object makeSemiMutable(Object obj) {
        if (obj instanceof List) {
            return new SemiMutableCollection<>(makeListElementsImmutable((List<Object>) obj));
        }

        if (obj instanceof Set) {
            return new SemiMutableCollection<>(makeSetElementsImmutable((Set<Object>) obj));
        }

        if (obj instanceof Map) {
            return new SemiMutableMap<>(makeMapEntriesImmutable((Map<Object, Object>) obj));
        }

        return makeImmutable(obj);
    }

    private boolean isImmutableProperty(String propertyPath) {
        return !mutablePropertyPaths.contains(propertyPath);
    }

    private final Set<String> semiMutablePropertyPaths;
    private final Set<String> mutablePropertyPaths;

    private static class SemiMutableMap<K, V> extends ForwardingMap<K, V> {
        public SemiMutableMap(Map<K, V> delegate) {
            this.delegate = checkNotNull(delegate, "delegate is required");
        }

        @Override
        protected Map<K, V> delegate() {
            return delegate;
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            map.keySet().forEach(key -> {
                if (containsKey(key)) {
                    throw new UnsupportedOperationException("Collection is frozen; cannot put key " + String.valueOf(key));
                }
            });

            super.putAll(map);
        }

        private final Map<K,V> delegate;
    }

    private static class SemiMutableCollection<T> extends ForwardingCollection<T> {
        public SemiMutableCollection(Collection<T> delegate) {
            this.delegate = checkNotNull(delegate, "delegate is required");
        }

        @Override
        protected Collection<T> delegate() {
            return delegate;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException("Collection is frozen");
        }

        @Override
        public boolean remove(Object object) {
            throw new UnsupportedOperationException("Collection is frozen");
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException("Collection is frozen");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Collection is frozen");
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            throw new UnsupportedOperationException("Collection is frozen");
        }

        private final Collection<T> delegate;
    }
}
