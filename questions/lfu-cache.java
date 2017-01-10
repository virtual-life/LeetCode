/**
Design and implement a data structure for Least Frequently Used (LFU) cache. It should support the following operations: get and set.

get(key) - Get the value (will always be positive) of the key if the key exists in the cache, otherwise return -1.
set(key, value) - Set or insert the value if the key is not already present. When the cache reaches its capacity, it should invalidate the least frequently used item before inserting a new item. For the purpose of this problem, when there is a tie (i.e., two or more keys that have the same frequency), the least recently used key would be evicted.

Follow up:
Could you do both operations in O(1) time complexity?

Example:

LFUCache cache = new LFUCache( 2 /* capacity */ );

cache.set(1, 1);
cache.set(2, 2);
cache.get(1);       // returns 1
cache.set(3, 3);    // evicts key 2
cache.get(2);       // returns -1 (not found)
cache.get(3);       // returns 3.
cache.set(4, 4);    // evicts key 1.
cache.get(1);       // returns -1 (not found)
cache.get(3);       // returns 3
cache.get(4);       // returns 4
*/


/**
O(1) for all of LFU cache operations, which include insertion, access and deletion(eviction)

It requires three data structures. One is a hash table which is used to cache the key/values so that given a key we can retrieve the cache entry at O(1). 

Second one is a double linked list for each frequency of access. 
The max frequency is capped at the cache size to avoid creating more and more frequency list entries. 
If we have a cache of max size 4 then we will end up with 4 different frequencies. 
Each frequency will have a double linked list to keep track of the cache entries belonging to that particular frequency.

The third data structure would be to somehow link these frequencies lists. 
It can be either an array or another linked list so that on accessing a cache entry it can be easily promoted to the next frequency list in time O(1). 
In our article it is based on array as traversing would be faster than linked list.
*/

/**
Reference - https://github.com/chirino/hawtdb/blob/master/hawtdb/src/main/java/org/fusesource/hawtdb/util/LFUCache.java
*/
    
/*
Frequency list is stored as an array with no next/prev pointers between nodes: looping over the array should be faster and more CPU-cache friendly than
using an ad-hoc linked-pointers structure.
*/

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;


public class LFUCache {

    private final Map<Integer, CacheNode> cache;
    private final LinkedHashSet[] frequencyList;
    private int lowestFrequency;
    private int maxFrequency;
    private final int maxCacheSize;

    // @param capacity, an integer
    public LFUCache(int capacity) {
        // Write your code here
        this.cache = new HashMap<Integer, CacheNode>(capacity);
        this.frequencyList = new LinkedHashSet[capacity * 2];
        this.lowestFrequency = 0;
        this.maxFrequency = capacity * 2 - 1;
        this.maxCacheSize = capacity;
        initFrequencyList();
    }

    // @param key, an integer
    // @param value, an integer
    // @return nothing
    public void set(int key, int value) {
        // Write your code here
        CacheNode currentNode = cache.get(key);
        if (currentNode == null) {
            if (cache.size() == maxCacheSize) {
                doEviction();
            }
            LinkedHashSet<CacheNode> nodes = frequencyList[0];
            currentNode = new CacheNode(key, value, 0);
            nodes.add(currentNode);
            cache.put(key, currentNode);
            lowestFrequency = 0;
        } else {
            currentNode.v = value;
        }
        addFrequency(currentNode);
    }

    public int get(int key) {
        // Write your code here
        CacheNode currentNode = cache.get(key);
        if (currentNode != null) {
            addFrequency(currentNode);
            return currentNode.v;
        } else {
            return -1;
        }
    }

    public void addFrequency(CacheNode currentNode) {
        int currentFrequency = currentNode.frequency;
        if (currentFrequency < maxFrequency) {
            int nextFrequency = currentFrequency + 1;
            LinkedHashSet<CacheNode> currentNodes = frequencyList[currentFrequency];
            LinkedHashSet<CacheNode> newNodes = frequencyList[nextFrequency];
            moveToNextFrequency(currentNode, nextFrequency, currentNodes, newNodes);
            cache.put(currentNode.k, currentNode);
            if (lowestFrequency == currentFrequency && currentNodes.isEmpty()) {
                lowestFrequency = nextFrequency;
            }
        } else {
            // Hybrid with LRU: put most recently accessed ahead of others:
            LinkedHashSet<CacheNode> nodes = frequencyList[currentFrequency];
            nodes.remove(currentNode);
            nodes.add(currentNode);
        }
    }

    public int remove(int key) {
        CacheNode currentNode = cache.remove(key);
        if (currentNode != null) {
            LinkedHashSet<CacheNode> nodes = frequencyList[currentNode.frequency];
            nodes.remove(currentNode);
            if (lowestFrequency == currentNode.frequency) {
                findNextLowestFrequency();
            }
            return currentNode.v;
        } else {
            return -1;
        }
    }

    public int frequencyOf(int key) {
        CacheNode node = cache.get(key);
        if (node != null) {
            return node.frequency + 1;
        } else {
            return 0;
        }
    }

    public void clear() {
        for (int i = 0; i <= maxFrequency; i++) {
            frequencyList[i].clear();
        }
        cache.clear();
        lowestFrequency = 0;
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    public boolean containsKey(int key) {
        return this.cache.containsKey(key);
    }

    private void initFrequencyList() {
        for (int i = 0; i <= maxFrequency; i++) {
            frequencyList[i] = new LinkedHashSet<CacheNode>();
        }
    }

    private void doEviction() {
        int currentlyDeleted = 0;
        double target = 1; // just one
        while (currentlyDeleted < target) {
            LinkedHashSet<CacheNode> nodes = frequencyList[lowestFrequency];
            if (nodes.isEmpty()) {
                continue;
            } else {
                Iterator<CacheNode> it = nodes.iterator();
                while (it.hasNext() && currentlyDeleted++ < target) {
                    CacheNode node = it.next();
                    it.remove();
                    cache.remove(node.k);
                }
                if (!it.hasNext()) {
                    findNextLowestFrequency();
                }
            }
        }
    }

    private void moveToNextFrequency(CacheNode currentNode, int nextFrequency,
                                     LinkedHashSet<CacheNode> currentNodes,
                                     LinkedHashSet<CacheNode> newNodes) {
        currentNodes.remove(currentNode);
        newNodes.add(currentNode);
        currentNode.frequency = nextFrequency;
    }

    private void findNextLowestFrequency() {
        while (lowestFrequency <= maxFrequency && frequencyList[lowestFrequency].isEmpty()) {
            lowestFrequency++;
        }
        if (lowestFrequency > maxFrequency) {
            lowestFrequency = 0;
        }
    }

    private class CacheNode {
        public final int k;
        public int v;
        public int frequency;

        public CacheNode(int k, int v, int frequency) {
            this.k = k;
            this.v = v;
            this.frequency = frequency;
        }

    }
}
