
// Mariana Hernandez

package com.company;

import java.util.*;
import java.lang.*;

public class TLB {

    // Using a hashtable for TLB mapping
    Hashtable table;
    LinkedList<Integer> list = new LinkedList<Integer>();

    // Constructor
    public TLB(){
        this.table=new Hashtable();
        for (int i = 0; i < 16; i++) {
            this.table.put(-i, -1); // key, value
            this.list.add(-i);
        }
    }

    // Obtains page number if it is in the TLB
    public int get(int p_num){
        if (this.table.containsKey(p_num))
            return (int) this.table.get(p_num);
        else
            return -1;
    }

    // Adds new translation
    public void put(int p_num, int f_num){
        Integer i = this.list.poll();
        if (i != null){
            this.table.remove(i.intValue()); // delete old data
        }

        this.list.add(p_num);
        this.table.put(p_num, f_num);
    }

    public static void main(String[] args) {
        TLB tlb = new TLB();
        for (int i = 0; i <= 16; i++) {
            tlb.put(i, i);
        }
        System.out.println(tlb.get(0));
    }
}
