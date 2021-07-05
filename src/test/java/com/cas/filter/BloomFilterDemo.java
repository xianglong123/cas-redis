package com.cas.filter;

import com.google.common.hash.Funnels;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;

/**
 * @author xiang_long
 * @version 1.0
 * @date 2021/7/5 3:27 下午
 * @desc 布隆过滤器
 */
public class BloomFilterDemo {
    public static void main(String[] args) {
        int total = 1000000; // 总数量
        BloomFilter<CharSequence> bf =
                BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), total, 0.0002);
        // 初始化 1000000 条数据到过滤器中
        for (int i = 0; i < total; i++) {
            bf.put("" + i);
        }
        // 判断值是否存在过滤器中
        int count = 0;
        for (int i = 0; i < total + 10000; i++) {
            if (bf.mightContain("" + i)) {
                count++;
            }
        }
        System.out.println("已匹配数量 " + count);
    }
}
