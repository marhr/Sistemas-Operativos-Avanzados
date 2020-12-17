
// Mariana Hernandez

package com.company;

public class PageTable {

    // The page table stores the mapping between virtual addresses and physical addresses
    PageTableItem[] table;

    // Constructor
    public PageTable(){
        table = new PageTableItem[256];
        for (int i = 0; i<256; i++){
            this.table[i] = new PageTableItem(-1, false);
        }
    }

    // This function obtains the frame number using page number
    public int get(int p_num){
        int frameNumber = this.table[p_num].getFrameNumber();
        if (frameNumber == -1)// the frame is not in the page table
            return -1;
        return frameNumber;
    }

    public void add(int p_num, int f_num){

        this.table[p_num] = new PageTableItem(f_num,true);
    }
}

// This class represent the content of the page table
class PageTableItem {

    int frameNumber; // frame number information
    boolean valid; // is the frame in memory?

    // Constructor
    public PageTableItem(int i, boolean b) {
        this.frameNumber = i;
        this.valid = b;
    }

    // This function gets the frame number of the current item
    public int getFrameNumber() {

        return this.frameNumber;
    }
}

class Superpage {

    PageTableItem[] superpage;
    int size;

    // Constructor with particular size
    public Superpage(int size){
        this.size = size;
        this.superpage = new PageTableItem[size];
    }

    // constructor with default size
    public Superpage(){
        this.size = 4;
        this.superpage = new PageTableItem[4];
    }

}