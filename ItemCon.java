package com.vmac.tasks.ops.util;

import java.util.List;

public class ItemCon
{
    Integer i;
    List<Integer> li;
    double c1;
    double c2;

    ItemCon(Integer i, List<Integer> li)
    {
        this.i = i;
        this.li = li;
    }

    public double getC1()
    {
        return c1;
    }

    void setC1(double c1)
    {
        this.c1 = c1;
    }

    public double getC2()
    {
        return c2;
    }

    void setC2(double c2)
    {
        this.c2 = c2;
    }

    public Integer getI()
    {
        return i;
    }

    public void setI(Integer i)
    {
        this.i = i;
    }

    public List<Integer> getLi()
    {
        return li;
    }

    public void setLi(List<Integer> li)
    {
        this.li = li;
    }

}
