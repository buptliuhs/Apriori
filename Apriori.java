package com.vmac.tasks.ops.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// import org.apache.log4j.Logger;

public class Apriori
{
    private int minNum; // > minNum
    private double minCon; // > minCon
    private List<Set<Integer>> records;
    private List<List<ItemSet>> result;
    private List<ItemSet> fis1; // frequent item set 1
    private Map<Integer, Integer> fis1Map; // frequent item set 1

    // private Logger logger = Logger.getLogger(Apriori.class);

    public Apriori(double minDegree, double minCon, List<String> data)
    {
        init(minDegree, minCon, data);
    }

    private void init(double minDegree, double minCon, List<String> data)
    {
        fis1 = new ArrayList<ItemSet>();
        fis1Map = new HashMap<Integer, Integer>();
        records = new ArrayList<Set<Integer>>();
        result = new ArrayList<List<ItemSet>>();

        for (String line : data)
        {
            Set<Integer> record;
            if (!"".equals(line.trim()))
            {
                record = new TreeSet<Integer>();
                String[] items = line.split(" ");
                for (String item : items)
                {
                    record.add(Integer.valueOf(item));
                }
                records.add(record);
            }
        }
        this.minCon = minCon;
        minNum = (int) (minDegree * records.size());
    }

    public int getMinNum()
    {
        return minNum;
    }

    public void setMinNum(int minNum)
    {
        this.minNum = minNum;
    }

    public double getMinCon()
    {
        return minCon;
    }

    public void setMinCon(double minCon)
    {
        this.minCon = minCon;
    }

    private void first()
    {
        Map<Integer, Integer> first = new HashMap<Integer, Integer>();
        for (Set<Integer> si : records)
            for (Integer i : si)
            {
                if (first.get(i) == null)
                    first.put(i, 1);
                else
                    first.put(i, first.get(i) + 1);
            }

        for (Integer i : first.keySet())
            if (first.get(i) > minNum)
            {
                fis1.add(new ItemSet(i, first.get(i)));
                fis1Map.put(i, first.get(i));
            }
    }

    private void loop(List<ItemSet> items, int level)
    {
        List<ItemSet> copy = new ArrayList<ItemSet>(items);
        List<ItemSet> res = new ArrayList<ItemSet>();
        int size = items.size();

        for (int i = 0; i < size; i++)
        {
            for (int j = i + 1; j < size; j++)
            {
                if (copy.get(i).canBeMerged(copy.get(j)))
                {
                    ItemSet is = new ItemSet(copy.get(i));
                    is.merge(copy.get(j).item.last());
                    res.add(is);
                }
            }
        }

        pruning(copy, res);

        if (res.size() != 0)
        {
            result.add(res);
            if (res.get(0).item.size() >= level)
                return;
            loop(res, level);
        }
    }

    private void pruning(List<ItemSet> pre, List<ItemSet> res)
    {
        if (res.size() == 0)
            return;
        // step 1 k项集的子集属于k-1项集
        Iterator<ItemSet> ir = res.iterator();
        while (ir.hasNext())
        {
            // 获取所有k-1项子集
            ItemSet now = ir.next();
            Map<Integer, List<Integer>> ss = subSet(now);
            // 判断是否在pre集中
            boolean flag = false;
            for (List<Integer> li : ss.values())
            {
                if (flag)
                    break;
                for (ItemSet pis : pre)
                {
                    if (pis.item.containsAll(li))
                    {
                        flag = false;
                        break;
                    }
                    flag = true;
                }
            }
            if (flag)
            {
                ir.remove();
                continue;
            }

            // step 2 支持度
            int i = 0;
            for (Set<Integer> sr : records)
            {
                if (sr.containsAll(now.item))
                    i++;

                now.support = i;
            }
            if (now.support <= minNum)
            {
                ir.remove();
                continue;
            }
            // 产生关联规则
            double deno = now.support;
            for (Map.Entry<Integer, List<Integer>> me : ss.entrySet())
            {
                ItemCon ic = new ItemCon(me.getKey(), me.getValue());

                int nume = 0;
                if (fis1Map.containsKey(ic.getI()))
                {
                    nume = fis1Map.get(ic.getI());
                    // logger.debug("fis1Map.get(" + ic.getI() + ") = " +
                    // fis1Map.get(ic.getI()));
                }

                // for (ItemSet f : fis1)
                // {
                // if (f.item.contains(me.getKey()))
                // {
                // nume = f.support;
                // logger.debug("f.item=" + f.item + ", " + f.support);
                // break;
                // }
                // }

                if (deno / nume > minCon)
                {
                    now.calcon(ic);
                    ic.setC1(deno / nume);
                }
                for (ItemSet pis : pre)
                    if (pis.item.size() == me.getValue().size() && pis.item.containsAll(me.getValue()))
                    {
                        nume = pis.support;
                        break;
                    }
                if (deno / nume > minCon)
                    ic.setC2(deno / nume);
            }
        }
    }

    private Map<Integer, List<Integer>> subSet(ItemSet is)
    {
        List<Integer> li = new ArrayList<Integer>(is.item);
        Map<Integer, List<Integer>> res = new HashMap<Integer, List<Integer>>();
        for (int i = 0, j = li.size(); i < j; i++)
        {
            List<Integer> _li = new ArrayList<Integer>(li);
            _li.remove(i);
            res.put(li.get(i), _li);
        }
        return res;
    }

    private void output()
    {
        PrintStream ps = new PrintStream(System.out);
        for (List<ItemSet> li : result)
        {
            ps.println("============= 频繁" + li.get(0).item.size() + "项集 =============");
            for (ItemSet is : li)
            {
                ps.println(is.item + " : " + is.support);
                ps.println();
                if (is.ics.size() != 0)
                {
                    ps.println("****** 关联规则 ******");
                    for (ItemCon ic : is.ics)
                    {
                        ps.println(ic.i + " ---> " + ic.li + " con: " + ic.c1);
                        if (ic.c2 > minCon)
                            ps.println(ic.li + " ---> " + ic.i + " con: " + ic.c2);
                    }
                    ps.println("******************");
                    ps.println();
                }
            }
            ps.println("=====================================");
        }

        ps.close();
    }

    public int getAppearanceCount(int i)
    {
        return fis1Map.containsKey(i) ? fis1Map.get(i) : 0;
    }

    public List<List<ItemSet>> getResult(int level)
    {
        result.clear();

        first();
        loop(fis1, level);

        return result;
    }

    public static void main(String[] args)
    {
        long begin = System.currentTimeMillis();
        Apriori apriori = new Apriori(0.25, 0.5, new LinkedList<String>());
        apriori.getResult(2);
        apriori.output();
        System.out.println("共耗时: " + ((System.currentTimeMillis()) - begin) + "ms");
    }
}
