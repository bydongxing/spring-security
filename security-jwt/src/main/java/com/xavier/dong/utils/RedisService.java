package com.xavier.dong.utils;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * redis的工具类
 *
 * @author dongxing
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_={@Autowired} )
public class RedisService {

    protected final RedisTemplate<String, Object> redisTemplate;

    /**
     * 出异常，重复操作的次数
     */
    private static final Integer TIMES = 3;

    /**
     * 用户排序通过注册时间的 权重值
     *
     * @param date
     * @return
     */
    public double getCreateTimeScore(long date) {
        return date / 100000.0;
    }


    /**
     * 获取Redis中所有的键的key
     *
     * @return
     */
    public Set<String> getAllKeys() {
        return this.redisTemplate.keys("*");
    }


    /**
     * 获取所有的普通key-value
     *
     * @return
     */
    public Map<String, Object> getAllString() {
        Set<String> stringSet = getAllKeys();
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.STRING) {
                map.put(k, get(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Set -key-value
     *
     * @return
     */
    public Map<String, Set<Object>> getAllSet() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.SET) {
                map.put(k, getSet(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的ZSet正序  -key-value 不获取权重值
     *
     * @return
     */
    public Map<String, Set<Object>> getAllZSetRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                log.debug("k:" + k);
                map.put(k, getZSetRange(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的ZSet倒序  -key-value 不获取权重值
     *
     * @return
     */
    public Map<String, Set<Object>> getAllZSetReverseRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                map.put(k, getZSetReverseRange(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的List -key-value
     *
     * @return
     */
    public Map<String, List<Object>> getAllList() {
        Set<String> stringSet = getAllKeys();
        Map<String, List<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.LIST) {
                map.put(k, getList(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Map -key-value
     *
     * @return
     */
    public Map<String, Map<String, Object>> getAllMap() {
        Set<String> stringSet = getAllKeys();
        Map<String, Map<String, Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.HASH) {
                map.put(k, getMap(k));
            }
        }
        return map;
    }

    /**
     * 添加一个list
     *
     * @param key
     * @param objectList
     */
    public void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }

    /**
     * 向list中增加值
     *
     * @param key
     * @param obj
     * @return 返回在list中的下标
     */
    public long addList(String key, Object obj) {
        return this.redisTemplate.boundListOps(key).rightPush(obj);
    }

    /**
     * 向list中增加值
     *
     * @param key
     * @param obj
     * @return 返回在list中的下标
     */
    public long addList(String key, Object... obj) {
        return this.redisTemplate.boundListOps(key).rightPushAll(obj);
    }

    /**
     * 输出list
     *
     * @param key List的key
     * @param s   开始下标
     * @param e   结束的下标
     * @return
     */
    public List<Object> getList(String key, long s, long e) {
        return this.redisTemplate.boundListOps(key).range(s, e);
    }

    /**
     * 输出完整的list
     *
     * @param key
     */
    public List<Object> getList(String key) {
        return this.redisTemplate.boundListOps(key).range(0, getListSize(key));
    }

    /**
     * 获取list集合中元素的个数
     *
     * @param key
     * @return
     */
    public long getListSize(String key) {
        return this.redisTemplate.boundListOps(key).size();
    }

    /**
     * 移除list中某值
     * 移除list中 count个value为object的值,并且返回移除的数量,
     * 如果count为0,或者大于list中为value为object数量的总和,
     * 那么移除所有value为object的值,并且返回移除数量
     *
     * @param key
     * @param object
     * @return 返回移除数量
     */
    public long removeListValue(String key, Object object) {
        return this.redisTemplate.boundListOps(key).remove(0, object);
    }


    /**
     * 移除list中某值
     *
     * @param key
     * @param objects
     * @return 返回移除数量
     */
    public long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }

    /**
     * 批量删除key对应的value
     *
     * @param keys
     */
    public void remove(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                remove(keys[0]);
            } else {
                this.redisTemplate.delete(CollectionUtils.arrayToList(keys));
            }
        }
    }

    /**
     * 删除缓存
     * 根据key精确匹配删除
     *
     * @param key
     */
    public void remove(String key) {
        if (exists(key)) {
            this.redisTemplate.delete(key);
        }
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blears
     */
    public void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 修改key名 如果不存在该key或者没有修改成功返回false
     *
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return this.redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blear
     */
    public void removeBlear(String blear) {
        this.redisTemplate.delete(this.redisTemplate.keys(blear));
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blears
     */
    public void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blear
     */
    public void removeByRegular(String blear) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                this.redisTemplate.delete(s);
            }
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param blears
     */
    public void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key
     * @param blear
     */
    public void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                this.redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }

    /**
     * 移除key 对应的value
     *
     * @param key
     * @param value
     * @return
     */
    public Long removeZSetValue(String key, Object... value) {
        return this.redisTemplate.boundZSetOps(key).remove(value);
    }

    /**
     * 移除key ZSet
     *
     * @param key
     * @return
     */
    public void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }

    /**
     * 删除，键为K的集合，索引start<=index<=end的元素子集
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public void removeZSetRange(String key, Long start, Long end) {
        this.redisTemplate.boundZSetOps(key).removeRange(start, end);
    }

    /**
     * 并集 将key对应的集合和key1对应的集合合并到key2中
     * 如果分数相同的值，都会保留
     * 原来key2的值会被覆盖
     *
     * @param key
     * @param key1
     * @param key2
     */
    public void setZSetUnionAndStore(String key, String key1, String key2) {
        this.redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }

    /**
     * 获取整个有序集合ZSET，正序
     *
     * @param key
     */
    public Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }

    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，正序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置
     */
    public Set<Object> getZSetRange(String key, long start, long end) {
        return this.redisTemplate.boundZSetOps(key).range(start, end);
    }

    /**
     * 获取整个有序集合ZSET，倒序
     *
     * @param key
     */
    public Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }

    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start<=index<=end的元素子集，倒序
     *
     * @param key
     * @param start 开始位置
     * @param end   结束位置
     */
    public Set<Object> getZSetReverseRange(String key, long start, long end) {
        return this.redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 正序 -从小到大
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return this.redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序 -从大到小
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return this.redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }

    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return this.redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }


    /**
     * 键为K的集合，索引start<=index<=end的元素子集
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return this.redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }

    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），正序
     *
     * @param key
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }

    /**
     * 键为K的集合
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }

    /**
     * 键为K的集合，sMin<=score<=sMax的元素个数
     *
     * @param key
     * @param sMin
     * @param sMax
     * @return
     */
    public long getZSetCountSize(String key, double sMin, double sMax) {
        return this.redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }

    /**
     * 获取Zset 键为K的集合元素个数
     *
     * @param key
     * @return
     */
    public long getZSetSize(String key) {
        return this.redisTemplate.boundZSetOps(key).size();
    }

    /**
     * 获取键为K的集合，value为obj的元素分数
     *
     * @param key
     * @param value
     * @return
     */
    public double getZSetScore(String key, Object value) {
        return this.redisTemplate.boundZSetOps(key).score(value);
    }

    /**
     * 元素分数增加，delta是增量
     *
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public double incrementZSetScore(String key, Object value, double delta) {
        return this.redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }

    /**
     * 自增1
     *
     * @param key
     * @return
     */
    public double incrementValue(String key) {
        return this.incrementValueWithDelta(key, 1);
    }


    /**
     * 自增,delta是增量
     *
     * @param key
     * @param delta
     * @return
     */
    public double incrementValueWithDelta(String key,double delta) {
        return this.redisTemplate.boundValueOps(key).increment(delta);
    }



    /**
     * 添加有序集合ZSET
     * 默认按照score升序排列，存储格式K(1)==V(n)，V(1)=S(1)
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Boolean addZSet(String key, Object value, double score) {
        return this.redisTemplate.boundZSetOps(key).add(value, score);
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param value
     * @return
     */
    public Long addZSet(String key, Set<ZSetOperations.TypedTuple<Object>> value) {
        return this.redisTemplate.boundZSetOps(key).add(value);
    }

    /**
     * 添加有序集合ZSET
     *
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Boolean addZSet(String key, double[] score, Object[] value) {
        if (score.length != value.length) {
            return Boolean.FALSE;
        }
        for (int i = 0; i < score.length; i++) {
            if (addZSet(key, value[i], score[i]).equals(Boolean.FALSE)) {
                return Boolean.FALSE;
            }
        }
        return true;
    }

    /**
     * 通过分数删除ZSet中的值
     *
     * @param key
     * @param s
     * @param e
     */
    public void removeZSetRangeByScore(String key, double s, double e) {
        this.redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }

    /**
     * 设置Set的过期时间
     *
     * @param key
     * @param time
     * @return
     */
    public Boolean setSetExpireTime(String key, Long time) {
        return this.redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }

    /**
     * 设置ZSet的过期时间
     *
     * @param key
     * @param time
     * @return
     */
    public Boolean setZSetExpireTime(String key, Long time) {
        return this.redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }


    /**
     * 判断缓存中是否有key对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key);
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     * @return
     */
    public Object get(int key) {
        return this.get(String.valueOf(key));
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     * @return
     */
    public Object get(long key) {
        return this.get(String.valueOf(key));
    }


    /**
     * 读取String缓存 可以是对象
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return this.redisTemplate.boundValueOps(key).get();
    }


    /**
     * 读取String缓存 可以是对象
     *
     * @param keys
     * @return
     */
    public List<Object> get(String... keys) {
        List<Object> list = new ArrayList<>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }


    /**
     * 读取缓存 可以是对象 根据正则表达式匹配
     *
     * @param regKey
     * @return
     */
    public List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(long key, Object value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(int key, Object value) {
        this.set(String.valueOf(key), value);
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key
     * @param value
     */
    public void set(String key, Object value) {
        this.redisTemplate.boundValueOps(key).set(value);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @param expireTime 过期时间 -单位s
     * @return
     */
    public void set(String key, Object value, Long expireTime) {
        this.redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key
     * @param expireTime
     * @return
     */
    public boolean setExpireTime(String key, Long expireTime) {
        return this.redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取key的类型
     *
     * @param key
     * @return
     */
    public DataType getType(String key) {
        return this.redisTemplate.type(key);
    }

    /**
     * 删除map中的某个对象
     *
     * @param key   map对应的key
     * @param field map中该对象的key
     */
    public void removeMapField(String key, Object... field) {
        this.redisTemplate.boundHashOps(key).delete(field);
    }

    /*
     * 获取map对象的数量
     * @param key map对应的key
     * @return
     */
    public Long getMapSize(String key) {
        return this.redisTemplate.boundHashOps(key).size();
    }

    /*
     * 获取map对象
     * @param key map对应的key
     * @return
     */
    public Map<String, Object> getMap(String key) {
        Map<Object, Object> entries = this.redisTemplate.boundHashOps(key).entries();
        return transform(entries, String::valueOf);

    }

    /**
     * convert {@code Map<K1,V>} to {@code Map<K2,V>}
     *
     * @return {@linkplain ImmutableMap}
     */
    public static <K1, K2, V> Map<K2, V> transform(Map<K1, V> fromMap, final Function<K1, K2> transformer) {
        checkNotNull(fromMap, "fromMap is null");
        checkNotNull(transformer, "transformer is null");
        // 新的Map对象Key类型已经是K2了，只是Value类型成了Entry
        ImmutableMap<K2, Entry<K1, V>> k2Entry = Maps.uniqueIndex(fromMap.entrySet(),
                e -> transformer.apply(e.getKey()));
        // 再做一次转换将Entry<K1, V>转换成V,这个过程并没有创建新的Map,只是创建了k2Entry的代理对象
        Map<K2, V> k2V = Maps.transformEntries(k2Entry,
                (k, v) -> v.getValue());
        return k2V;
    }

    /**
     * 获取map缓存中的某个对象
     *
     * @param key   map对应的key
     * @param field map中该对象的key
     * @return
     */
    public <T> T getMapField(String key, String field) {
        return (T) this.redisTemplate.boundHashOps(key).get(field);
    }

    /**
     * 判断map中对应key的key是否存在
     *
     * @param key map对应的key
     * @return
     */
    public Boolean hasMapKey(String key, String field) {
        return this.redisTemplate.boundHashOps(key).hasKey(field);
    }

    /**
     * 获取map对应key的value
     *
     * @param key map对应的key
     * @return
     */
    public List<Object> getMapFieldValue(String key) {
        return this.redisTemplate.boundHashOps(key).values();
    }

    /**
     * 获取map的key
     *
     * @param key map对应的key
     * @return
     */
    public Set<Object> getMapFieldKey(String key) {
        return this.redisTemplate.boundHashOps(key).keys();
    }

    /**
     * 添加map
     *
     * @param key
     * @param map
     */
    public void addMap(String key, Map<String, Object> map) {
        this.redisTemplate.boundHashOps(key).putAll(map);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     */
    public void addMap(String key, String field, Object value) {
        this.redisTemplate.boundHashOps(key).put(field, value);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param time  过期时间-整个MAP的过期时间
     * @param value 值
     */
    public void addMap(String key, String field, Object value, long time) {
        this.redisTemplate.boundHashOps(key).put(field, value);
        this.redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }

    /**
     * 处理事务时锁定key
     *
     * @param key
     */
    public void watch(String key) {
        this.redisTemplate.watch(key);
    }

    /**
     * 向set中加入对象
     *
     * @param key 对象key
     * @param obj 值
     */
    public void addSet(String key, Object... obj) {
        this.redisTemplate.boundSetOps(key).add(obj);
    }


    /**
     * 移除set中的某些值
     *
     * @param key 对象key
     * @param obj 值
     */
    public long removeSetValue(String key, Object obj) {
        return this.redisTemplate.boundSetOps(key).remove(obj);
    }

    /**
     * 移除set中的某些值
     *
     * @param key 对象key
     * @param obj 值
     */
    public long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return this.redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }

    /**
     * 获取set的对象数
     *
     * @param key 对象key
     */
    public long getSetSize(String key) {
        return this.redisTemplate.boundSetOps(key).size();
    }

    /**
     * 判断set中是否存在这个值
     *
     * @param key 对象key
     */
    public Boolean hasSetValue(String key, Object obj) {
        Boolean flag = Boolean.FALSE;
        int t = 0;
        for (; ; ) {
            try {
                flag = this.redisTemplate.boundSetOps(key).isMember(obj);
                if (Boolean.FALSE.equals(flag) || t > TIMES) {
                    break;
                }
            } catch (Exception e) {
                log.error("key[" + key + "],obj[" + obj + "]判断Set中的值是否存在失败,异常信息:" + e.getMessage());
                t++;
            }
        }
        log.info("key[" + key + "],obj[" + obj + "]是否存在,flag: [{}]", flag);
        return flag;
    }

    /**
     * 获得整个set
     *
     * @param key 对象key
     */
    public Set<Object> getSet(String key) {
        return this.redisTemplate.boundSetOps(key).members();
    }

    /**
     * 获得set 并集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> getSetUnion(String key, String otherKey) {
        return this.redisTemplate.boundSetOps(key).union(otherKey);
    }

    /**
     * 获得set 并集
     *
     * @param key
     * @param keys
     * @return
     */
    public Set<Object> getSetUnion(String key, Collection<String> keys) {
        return this.redisTemplate.boundSetOps(key).union(keys);
    }

    /**
     * 获得set 交集
     *
     * @param key
     * @param otherKey
     * @return
     */
    public Set<Object> getSetIntersect(String key, String otherKey) {
        return this.redisTemplate.boundSetOps(key).intersect(otherKey);
    }

    /**
     * 获得set 交集
     *
     * @param key
     * @param keys
     * @return
     */
    public Set<Object> getSetIntersect(String key, Collection<String> keys) {
        return this.redisTemplate.boundSetOps(key).intersect(keys);
    }

}
