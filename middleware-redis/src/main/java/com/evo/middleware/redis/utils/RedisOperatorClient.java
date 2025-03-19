package com.evo.middleware.redis.utils;

import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisOperatorClient {

    private final BaseOperator baseOperator;
    private final StringOperator stringOperator;
    private final HashOperator hashOperator;
    private final ListOperator listOperator;
    private final SetOperator setOperator;
    private final ZSetOperator zsetOperator;
    private final ByteArrayOperator byteArrayOperator;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    public RedisOperatorClient(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        this.baseOperator = new BaseOperator(redisTemplate);
        this.stringOperator = new StringOperator(redisTemplate);
        this.listOperator = new ListOperator(redisTemplate);
        this.setOperator = new SetOperator(redisTemplate);
        this.zsetOperator = new ZSetOperator(redisTemplate);
        this.hashOperator = new HashOperator(redisTemplate);
        this.byteArrayOperator = new ByteArrayOperator(redisTemplate);
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    public BaseOperator base() {
        return baseOperator;
    }

    public StringOperator string() {
        return stringOperator;
    }

    public HashOperator hash() {
        return hashOperator;
    }

    public ListOperator list() {
        return listOperator;
    }

    public SetOperator set() {
        return setOperator;
    }

    public ZSetOperator zset() {
        return zsetOperator;
    }

    public ByteArrayOperator byteArray() {
        return byteArrayOperator;
    }

    public RedisTemplate<String, Object> redisTemplate(){
        return redisTemplate;
    }

    public RedissonClient redissonClient(){
        return redissonClient;
    }

    public static class BaseOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public BaseOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 是否存在key
         */
        public Boolean hasKey(String key) {
            return redisTemplate.hasKey(key);
        }

        /**
         * 查找匹配的key
         */
        public Set<String> keys(String pattern) {
            return redisTemplate.keys(pattern);
        }

        /**
         * 设置过期时间
         */
        public Boolean expire(String key, long timeout, TimeUnit unit) {
            return redisTemplate.expire(key, timeout, unit);
        }

        /**
         * 设置过期时间
         */
        public Boolean expireAt(String key, Date date) {
            return redisTemplate.expireAt(key, date);
        }

        /**
         * 移除 key 的过期时间，key 将持久保持
         */
        public Boolean persist(String key) {
            return redisTemplate.persist(key);
        }

        /**
         * 返回 key 的剩余的过期时间
         */
        public Long getExpire(String key, TimeUnit unit) {
            return redisTemplate.getExpire(key, unit);
        }

        public void delete(String key){
            redisTemplate.delete(key);
        }

        public void delete(Collection<String> keys){
            redisTemplate.delete(keys);
        }

        /**
         * 返回 key 的剩余的过期时间
         *
         * @param key 键
         * @return 过期时间
         */
        public Long getExpire(String key) {
            return redisTemplate.getExpire(key);
        }

        /**
         * 从当前数据库中随机返回一个 key
         */
        public String randomKey() {
            return redisTemplate.randomKey();
        }

        /**
         * 修改 key 的名称
         */
        public void rename(String oldKey, String newKey) {
            redisTemplate.rename(oldKey, newKey);
        }

        /**
         * 将当前数据库的 key 移动到给定的数据库 db 当中
         */
        public Boolean move(String key, int dbIndex) {
            return redisTemplate.move(key, dbIndex);
        }

        /**
         * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
         */
        public Boolean renameIfAbsent(String oldKey, String newKey) {
            return redisTemplate.renameIfAbsent(oldKey, newKey);
        }

        /**
         * 返回 key 所储存的值的类型
         */
        public DataType type(String key) {
            return redisTemplate.type(key);
        }
    }

    public static class StringOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public StringOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 设置指定 key 的值
         */
        public void set(String key, Object value) {
            redisTemplate.opsForValue().set(key, value);
        }

        /**
         * 设置指定 key 的值
         */
        public void set(String key, Object value, long timeout, TimeUnit unit) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        }

        /**
         * 获取指定 key 的值
         */
        public Object get(String key) {
            return redisTemplate.opsForValue().get(key);
        }

        /**
         * 返回 key 中字符串值的子字符
         */
        public Object getRange(String key, long start, long end) {
            return redisTemplate.opsForValue().get(key, start, end);
        }

        /**
         * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
         */
        public Object getAndSet(String key, Object value) {
            return redisTemplate.opsForValue().getAndSet(key, value);
        }

        /**
         * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
         */
        public Boolean getBit(String key, long offset) {
            return redisTemplate.opsForValue().getBit(key, offset);
        }

        /**
         * 批量获取
         */
        public List<Object> multiGet(Collection<String> keys) {
            return redisTemplate.opsForValue().multiGet(keys);
        }

        /**
         * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
         */
        public Boolean setBit(String key, long offset, boolean value) {
            return redisTemplate.opsForValue().setBit(key, offset, value);
        }

        /**
         * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
         */
        public void setEx(String key, Object value, long timeout, TimeUnit unit) {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        }

        /**
         * 只有在 key 不存在时设置 key 的值
         */
        public Boolean setIfAbsent(String key, Object value) {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        }

        /**
         * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
         */
        public void setRange(String key, Object value, long offset) {
            redisTemplate.opsForValue().set(key, value, offset);
        }

        /**
         * 获取字符串的长度
         */
        public Long size(String key) {
            return redisTemplate.opsForValue().size(key);
        }

        /**
         * 批量添加
         */
        public void multiSet(Map<String, Object> maps) {
            redisTemplate.opsForValue().multiSet(maps);
        }

        /**
         * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
         */
        public Boolean multiSetIfAbsent(Map<String, Object> maps) {
            return redisTemplate.opsForValue().multiSetIfAbsent(maps);
        }

        /**
         * 增加(自增长), 负数则为自减
         */
        public Long incrBy(String key, long increment) {
            return redisTemplate.opsForValue().increment(key, increment);
        }

        /**
         * 增加(自增长), 负数则为自减
         */
        public Double incrByFloat(String key, double increment) {
            return redisTemplate.opsForValue().increment(key, increment);
        }

        /**
         * 追加到末尾
         */
        public Integer append(String key, String value) {
            return redisTemplate.opsForValue().append(key, value);
        }
    }

    public static class HashOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public HashOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 获取存储在哈希表中指定字段的值
         */
        public Object get(String key, String field) {
            return redisTemplate.opsForHash().get(key, field);
        }

        /**
         * 获取所有给定字段的值
         */
        public Map<Object, Object> getAll(String key) {
            return redisTemplate.opsForHash().entries(key);
        }

        /**
         * 获取所有给定字段的值
         */
        public List<Object> multiGet(String key, Collection<Object> fields) {
            return redisTemplate.opsForHash().multiGet(key, fields);
        }

        /**
         * 增加一个哈希表字段
         */
        public void put(String key, String hashKey, String value) {
            redisTemplate.opsForHash().put(key, hashKey, value);
        }

        /**
         * 增加多个哈希表字段
         */
        public void putAll(String key, Map<String, String> maps) {
            redisTemplate.opsForHash().putAll(key, maps);
        }

        /**
         * 仅当hashKey不存在时才设置
         */
        public Boolean putIfAbsent(String key, String hashKey, String value) {
            return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
        }

        /**
         * 删除一个或多个哈希表字段
         */
        public Long delete(String key, Object... fields) {
            return redisTemplate.opsForHash().delete(key, fields);
        }

        /**
         * 查看哈希表 key 中，指定的字段是否存在
         */
        public Boolean exists(String key, String field) {
            return redisTemplate.opsForHash().hasKey(key, field);
        }

        /**
         * 为哈希表 key 中的指定字段的整数值加上增量 increment
         */
        public Long incrBy(String key, Object field, long increment) {
            return redisTemplate.opsForHash().increment(key, field, increment);
        }

        /**
         * 为哈希表 key 中的指定字段的整数值加上增量 increment
         */
        public Double incrByFloat(String key, Object field, double delta) {
            return redisTemplate.opsForHash().increment(key, field, delta);
        }

        /**
         * 获取所有哈希表中的字段
         */
        public Set<Object> keys(String key) {
            return redisTemplate.opsForHash().keys(key);
        }

        /**
         * 获取哈希表中字段的数量
         */
        public Long size(String key) {
            return redisTemplate.opsForHash().size(key);
        }

        /**
         * 获取哈希表中所有值
         */
        public List<Object> values(String key) {
            return redisTemplate.opsForHash().values(key);
        }

        /**
         * 迭代哈希表中的键值对
         */
        public Cursor<Map.Entry<Object, Object>> hScan(String key, ScanOptions options) {
            return redisTemplate.opsForHash().scan(key, options);
        }
    }

    public static class ListOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public ListOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 通过索引获取列表中的元素
         */
        public Object lIndex(String key, long index) {
            return redisTemplate.opsForList().index(key, index);
        }

        /**
         * 获取列表指定范围内的元素
         */
        public List<Object> lRange(String key, long start, long end) {
            return redisTemplate.opsForList().range(key, start, end);
        }

        /**
         * 存储在list头部
         */
        public Long lLeftPush(String key, Object value) {
            return redisTemplate.opsForList().leftPush(key, value);
        }

        /**
         * 存储在list头部
         */
        public Long lLeftPushAll(String key, Object... value) {
            return redisTemplate.opsForList().leftPushAll(key, value);
        }

        /**
         * 存储在list头部
         */
        public Long lLeftPushAll(String key, Collection<Object> value) {
            return redisTemplate.opsForList().leftPushAll(key, value);
        }

        /**
         * 当list存在的时候才加入
         */
        public Long lLeftPushIfPresent(String key, Object value) {
            return redisTemplate.opsForList().leftPushIfPresent(key, value);
        }

        /**
         * 如果pivot存在,再pivot前面添加
         */
        public Long lLeftPush(String key, String pivot, Object value) {
            return redisTemplate.opsForList().leftPush(key, pivot, value);
        }

        /**
         * 存储在list尾部
         */
        public Long lRightPush(String key, Object value) {
            return redisTemplate.opsForList().rightPush(key, value);
        }

        /**
         * 存储在list尾部
         */
        public Long lRightPushAll(String key, Object... value) {
            return redisTemplate.opsForList().rightPushAll(key, value);
        }

        /**
         * 存储在list尾部
         */
        public Long lRightPushAll(String key, Collection<Object> value) {
            return redisTemplate.opsForList().rightPushAll(key, value);
        }

        /**
         * 为已存在的列表添加值
         */
        public Long lRightPushIfPresent(String key, Object value) {
            return redisTemplate.opsForList().rightPushIfPresent(key, value);
        }

        /**
         * 在pivot元素的右边添加值
         */
        public Long lRightPush(String key, String pivot, Object value) {
            return redisTemplate.opsForList().rightPush(key, pivot, value);
        }

        /**
         * 通过索引设置列表元素的值
         */
        public void lSet(String key, long index, Object value) {
            redisTemplate.opsForList().set(key, index, value);
        }

        /**
         * 移出并获取列表的第一个元素
         */
        public Object lLeftPop(String key) {
            return redisTemplate.opsForList().leftPop(key);
        }

        /**
         * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
         */
        public Object lLeftPop(String key, long timeout, TimeUnit unit) {
            return redisTemplate.opsForList().leftPop(key, timeout, unit);
        }

        /**
         * 移除并获取列表最后一个元素
         */
        public Object lRightPop(String key) {
            return redisTemplate.opsForList().rightPop(key);
        }

        /**
         * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
         */
        public Object lRightPop(String key, long timeout, TimeUnit unit) {
            return redisTemplate.opsForList().rightPop(key, timeout, unit);
        }

        /**
         * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
         */
        public Object lRightPopAndLeftPush(String sourceKey, String destinationKey) {
            return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
        }

        /**
         * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
         */
        public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
            return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
        }

        /**
         * 删除集合中值等于value得元素
         *
         * @param key   键
         * @param index index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
         *              index<0, 从尾部开始删除第一个值等于value的元素;
         * @param value 值
         * @return list长度
         */
        public Long lRemove(String key, long index, Object value) {
            return redisTemplate.opsForList().remove(key, index, value);
        }

        /**
         * 裁剪list
         */
        public void lTrim(String key, long start, long end) {
            redisTemplate.opsForList().trim(key, start, end);
        }

        /**
         * 获取列表长度
         */
        public Long lLen(String key) {
            return redisTemplate.opsForList().size(key);
        }
    }

    public static class SetOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public SetOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * set添加元素
         *
         * @param key    键
         * @param values 多个值（可选）
         * @return 添加到集合中的元素数量，不包括已经存在于集合中的所有元素
         */
        public Long sAdd(String key, Object... values) {
            return redisTemplate.opsForSet().add(key, values);
        }

        /**
         * set移除元素
         *
         * @param key    键
         * @param values 多个值
         * @return 从集合中删除的成员数，不包括非现有成员。
         */
        public Long sRemove(String key, Object... values) {
            return redisTemplate.opsForSet().remove(key, values);
        }

        /**
         * 移除并返回集合的一个随机元素
         *
         * @param key 键
         * @return 随机元素
         */
        public String sPop(String key) {
            return (String) redisTemplate.opsForSet().pop(key);
        }

        /**
         * 将元素value从一个集合移到另一个集合
         *
         * @param key     键
         * @param value   值
         * @param destKey 目标key
         * @return 如果元素被移动，则为true。 如果元素不是源成员且未执行任何操作，则为false。
         */
        public Boolean sMove(String key, Object value, String destKey) {
            return redisTemplate.opsForSet().move(key, value, destKey);
        }

        /**
         * 获取集合的大小
         *
         * @param key 键
         * @return set长度
         */
        public Long sSize(String key) {
            return redisTemplate.opsForSet().size(key);
        }

        /**
         * 判断集合是否包含value
         *
         * @param key   键
         * @param value 值
         * @return 是否存在
         */
        public Boolean sIsMember(String key, Object value) {
            return redisTemplate.opsForSet().isMember(key, value);
        }

        /**
         * 获取两个集合的交集
         *
         * @param key      键一
         * @param otherKey 键二
         * @return 交集
         */
        public Set<Object> sIntersect(String key, String otherKey) {
            return redisTemplate.opsForSet().intersect(key, otherKey);
        }

        /**
         * 获取key集合与多个集合的交集
         *
         * @param key       键一
         * @param otherKeys 多个其他键
         * @return 交集
         */
        public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
            return redisTemplate.opsForSet().intersect(key, otherKeys);
        }

        /**
         * key集合与otherKey集合的交集存储到destKey集合中
         *
         * @param key      键一
         * @param otherKey 键二
         * @param destKey  目标键
         * @return set长度
         */
        public Long sIntersectAndStore(String key, String otherKey, String destKey) {
            return redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
        }

        /**
         * key集合与多个集合的交集存储到destKey集合中
         *
         * @param key       键一
         * @param otherKeys 多个其他键
         * @param destKey   目标键
         * @return 结果集中的元素数
         */
        public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
            return redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
        }

        /**
         * 获取两个集合的并集
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @return 并集
         */
        public Set<Object> sUnion(String key, String otherKeys) {
            return redisTemplate.opsForSet().union(key, otherKeys);
        }

        /**
         * 获取key集合与多个集合的并集
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @return 并集
         */
        public Set<Object> sUnion(String key, Collection<String> otherKeys) {
            return redisTemplate.opsForSet().union(key, otherKeys);
        }

        /**
         * key集合与otherKey集合的并集存储到destKey中
         *
         * @param key      键一
         * @param otherKey 键二
         * @param destKey  目标键
         * @return 结果集中的元素数
         */
        public Long sUnionAndStore(String key, String otherKey, String destKey) {
            return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
        }

        /**
         * key集合与多个集合的并集存储到destKey中
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @param destKey   目标键
         * @return 结果集中的元素数
         */
        public Long sUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
            return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
        }

        /**
         * 获取两个集合的差集
         *
         * @param key      键一
         * @param otherKey 键二
         * @return 差集
         */
        public Set<Object> sDifference(String key, String otherKey) {
            return redisTemplate.opsForSet().difference(key, otherKey);
        }

        /**
         * 获取key集合与多个集合的差集
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @return 差集
         */
        public Set<Object> sDifference(String key, Collection<String> otherKeys) {
            return redisTemplate.opsForSet().difference(key, otherKeys);
        }

        /**
         * key集合与otherKey集合的差集存储到destKey中
         *
         * @param key      键一
         * @param otherKey 键二
         * @param destKey  目标键
         * @return 结果集中的元素数
         */
        public Long sDifference(String key, String otherKey, String destKey) {
            return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
        }

        /**
         * key集合与多个集合的差集存储到destKey中
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @param destKey   目标键
         * @return 结果集中的元素数
         */
        public Long sDifference(String key, Collection<String> otherKeys, String destKey) {
            return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
        }

        /**
         * 获取集合所有元素
         *
         * @param key 键
         * @return 所有元素
         */
        public Set<Object> setMembers(String key) {
            return redisTemplate.opsForSet().members(key);
        }

        /**
         * 随机获取集合中的一个元素
         *
         * @param key 键
         * @return 随机元素
         */
        public Object sRandomMember(String key) {
            return redisTemplate.opsForSet().randomMember(key);
        }

        /**
         * 随机获取集合中count个元素
         *
         * @param key   键
         * @param count 数量
         * @return 随机元素list
         */
        public List<Object> sRandomMembers(String key, long count) {
            return redisTemplate.opsForSet().randomMembers(key, count);
        }

        /**
         * 随机获取集合中count个元素并且去除重复的
         *
         * @param key   键
         * @param count 数量
         * @return 随机元素set
         */
        public Set<Object> sDistinctRandomMembers(String key, long count) {
            return redisTemplate.opsForSet().distinctRandomMembers(key, count);
        }

        /**
         * 获取Cursor
         *
         * @param key     键
         * @param options 扫描选项
         * @return 游标
         */
        public Cursor<Object> sScan(String key, ScanOptions options) {
            return redisTemplate.opsForSet().scan(key, options);
        }

    }

    public static class ZSetOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public ZSetOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 添加元素,有序集合是按照元素的score值由小到大排列
         *
         * @param key   键
         * @param value 值
         * @param score 分数
         * @return 是否成功
         */
        public Boolean zAdd(String key, Object value, double score) {
            return redisTemplate.opsForZSet().add(key, value, score);
        }

        /**
         * 添加多个元素
         *
         * @param key    键
         * @param values 多个值
         * @return 添加的数量
         */
        public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> values) {
            return redisTemplate.opsForZSet().add(key, values);
        }

        /**
         * 移除元素
         *
         * @param key    键
         * @param values 多个值（可选）
         * @return 删除的数量
         */
        public Long zRemove(String key, Object... values) {
            return redisTemplate.opsForZSet().remove(key, values);
        }

        /**
         * 增加元素的score值，并返回增加后的值
         *
         * @param key   键
         * @param value 值
         * @param delta delta
         * @return 成员的新分数（双精度浮点数），表示为字符串
         */
        public Double zIncrementScore(String key, Object value, double delta) {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        }

        /**
         * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
         *
         * @param key   键
         * @param value 值
         * @return 0表示第一位
         */
        public Long zRank(String key, Object value) {
            return redisTemplate.opsForZSet().rank(key, value);
        }

        /**
         * 返回元素在集合的排名,按元素的score值由大到小排列
         *
         * @param key   键
         * @param value 值
         * @return 0表示第一位
         */
        public Long zReverseRank(String key, Object value) {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        }

        /**
         * 获取集合的元素, 从小到大排序
         *
         * @param key   键
         * @param start 开始位置
         * @param end   结束位置, -1查询所有
         */
        public Set<Object> zRange(String key, long start, long end) {
            return redisTemplate.opsForZSet().range(key, start, end);
        }

        /**
         * 获取集合元素, 并且把score值也获取
         *
         * @param key   键
         * @param start 开始下标
         * @param end   结束下标
         * @return 集合元素
         */
        public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        }

        /**
         * 根据Score值查询集合元素
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         * @return 集合元素
         */
        public Set<Object> zRangeByScore(String key, double min, double max) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }

        /**
         * 根据Score值查询集合元素, 从小到大排序
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         * @return 集合元素
         */
        public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        }

        /**
         * 根据Score值查询集合元素, 从小到大排序
         *
         * @param key   键
         * @param min   最小值
         * @param max   最大值
         * @param start 开始下标
         * @param end   结束下标
         * @return 集合元素
         */
        public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max, long start, long end) {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end);
        }

        /**
         * 获取集合的元素, 从大到小排序
         *
         * @param key   键
         * @param start 开始下标
         * @param end   结束下标
         * @return 集合元素
         */
        public Set<Object> zReverseRange(String key, long start, long end) {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        }

        /**
         * 获取集合的元素, 从大到小排序, 并返回score值
         *
         * @param key   键
         * @param start 开始下标
         * @param end   结束下标
         * @return 集合元素
         */
        public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long start, long end) {
            return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        }

        /**
         * 根据Score值查询集合元素, 从大到小排序
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         * @return 集合元素
         */
        public Set<Object> zReverseRangeByScore(String key, double min, double max) {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
        }

        /**
         * 根据Score值查询集合元素, 从大到小排序
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         * @return 集合元素
         */
        public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(
                String key, double min, double max) {
            return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max);
        }

        /**
         * 根据Score值查询集合元素, 从大到小排序
         *
         * @param key   键
         * @param min   最小值
         * @param max   最大值
         * @param start 开始下标
         * @param end   结束下标
         * @return 集合元素
         */
        public Set<Object> zReverseRangeByScore(String key, double min, double max, long start, long end) {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max,
                    start, end);
        }

        /**
         * 根据score值获取集合元素数量
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         * @return 数量
         */
        public Long zCount(String key, double min, double max) {
            return redisTemplate.opsForZSet().count(key, min, max);
        }

        /**
         * 获取集合大小
         *
         * @param key 键
         * @return 集合大小
         */
        public Long zSize(String key) {
            return redisTemplate.opsForZSet().size(key);
        }

        /**
         * 获取集合大小
         *
         * @param key 键
         * @return 集合大小
         */
        public Long zCard(String key) {
            return redisTemplate.opsForZSet().zCard(key);
        }

        /**
         * 获取集合中value元素的score值
         *
         * @param key   键
         * @param value 值
         * @return score值
         */
        public Double zScore(String key, Object value) {
            return redisTemplate.opsForZSet().score(key, value);
        }

        /**
         * 移除指定索引位置的成员
         *
         * @param key   键
         * @param start 开始下标
         * @param end   结束下标
         * @return 值
         */
        public Long zRemoveRange(String key, long start, long end) {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        }

        /**
         * 根据指定的score值的范围来移除成员
         *
         * @param key 键
         * @param min 最小值
         * @param max 最大值
         */
        public Long zRemoveRangeByScore(String key, double min, double max) {
            return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        }

        /**
         * 获取key和otherKey的并集并存储在destKey中
         *
         * @param key      键一
         * @param otherKey 键二
         * @param destKey  目标键
         * @return 结果集中的元素数
         */
        public Long zUnionAndStore(String key, String otherKey, String destKey) {
            return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
        }

        /**
         * 获取key和otherKey的并集并存储在destKey中
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @param destKey   目标键
         * @return 结果集中的元素数
         */
        public Long zUnionAndStore(String key, Collection<String> otherKeys, String destKey) {
            return redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
        }

        /**
         * 交集
         *
         * @param key      键一
         * @param otherKey 键二
         * @param destKey  目标键
         * @return 结果集中的元素数
         */
        public Long zIntersectAndStore(String key, String otherKey, String destKey) {
            return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
        }

        /**
         * 交集
         *
         * @param key       键
         * @param otherKeys 多个其他键
         * @param destKey   目标键
         * @return 结果集中的元素数
         */
        public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
            return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
        }

        /**
         * 获取Cursor
         *
         * @param key     键
         * @param options 扫描选项
         * @return 游标
         */
        public Cursor<ZSetOperations.TypedTuple<Object>> zScan(String key, ScanOptions options) {
            return redisTemplate.opsForZSet().scan(key, options);
        }
    }

    public static class ByteArrayOperator {
        private final RedisTemplate<String, Object> redisTemplate;

        public ByteArrayOperator(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        /**
         * 导出数据
         * 返回一个包含键值对二进制表示的字节数组（byte[]）。如果键不存在，则返回 null。
         */
        public byte[] dump(String key) {
            return redisTemplate.dump(key);
        }

        /**
         * 恢复数据
         * 将导出的二进制数据通过 RedisTemplate.restore 恢复到目标 Redis 实例中
         */
        public void restore(String key, long ttlInMilliseconds, byte[] dumpedData, TimeUnit unit, boolean replace) {
            redisTemplate.restore(key, dumpedData, ttlInMilliseconds, unit, replace);
        }
    }

}
