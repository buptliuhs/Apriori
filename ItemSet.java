package com.vmac.tasks.ops.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class ItemSet
{
    TreeSet<Integer> item;
    int support;
    List<ItemCon> ics = new ArrayList<ItemCon>();

    ItemSet(ItemSet is)
    {
        this.item = new TreeSet<Integer>(is.item);
    }

    public TreeSet<Integer> getItem()
    {
        return item;
    }

    public void setItem(TreeSet<Integer> item)
    {
        this.item = item;
    }

    public int getSupport()
    {
        return support;
    }

    public void setSupport(int support)
    {
        this.support = support;
    }

    public List<ItemCon> getIcs()
    {
        return ics;
    }

    public void setIcs(List<ItemCon> ics)
    {
        this.ics = ics;
    }

    ItemSet()
    {
        item = new TreeSet<Integer>();
    }

    ItemSet(int i, int v)
    {
        this();
        merge(i);
        setValue(v);
    }

    void setValue(int i)
    {
        this.support = i;
    }

    void merge(int i)
    {
        item.add(i);
    }

    void calcon(ItemCon ic)
    {
        ics.add(ic);
    }

    boolean canBeMerged(ItemSet other)
    {
        if (other == null || other.item.size() != item.size())
            return false;

        Iterator<Integer> i = item.iterator();
        Iterator<Integer> o = other.item.iterator();
        int n = item.size();
        while (i.hasNext() && o.hasNext() && --n > 0)
            if (i.next() != o.next())
                return false;

        return !(item.last() == other.item.last());
    }
}
