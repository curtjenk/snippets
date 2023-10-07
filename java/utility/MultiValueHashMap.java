

public class MultiValueHashMap<K, V> {
    private final HashMap<K, ArrayList<V>> map = new HashMap<>();
    public void put(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public List<V> get(K key) {
        return map.getOrDefault(key, new ArrayList<>());
    }

    public void remove(K key, V value) {
        map.computeIfPresent(key, (k, v) -> {
            v.remove(value);
            return v;
        });
    }

    public int size() {
        return map.size();
    }

    public void clear(K key) {
        map.computeIfPresent(key, (k, v) -> {
            v.clear();
            return v;
        });
    }

    public void clear() {
        map.clear();
    }

    public Set<K> keySet() {
        return map.keySet();
    }
}