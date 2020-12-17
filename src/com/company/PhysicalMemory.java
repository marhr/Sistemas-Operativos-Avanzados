
// Mariana Hernandez

package com.company;

import java.awt.*;

public class PhysicalMemory {

    static Frame[] frames;
    int free_frame; // index of next free frame; used in regular paging
    int free_frames_counter = 128;
    Buddy buddy;

    // constructor
    public PhysicalMemory(){
        this.frames = new Frame[128];
        this.free_frame = 0;
        this.buddy = new Buddy(128);
    }

    // This function adds a frame to the next free frame in memory
    // The function returns the position of the added frame
    public int addFrame(Frame f){
        this.frames[this.free_frame] = new Frame(f.data);
        this.free_frame ++ ;
        return this.free_frame-1; // the position just occupied

    }

    // Function returns the content in the location in memory
    public int getValue(int f_num, int offset){
        Frame frame = this.frames[f_num];
        return frame.data[offset];
    }

    // This function reserves the SP of given size
    // Return the number of still free frames
    public int reserveSuperpage(int superpage_size){
        // llamar buddy allocator
        int sp_index = this.buddy.allocate(superpage_size); // Returns the index where the potential superpage begins
        if(sp_index != -1){ // There is place for the SP
            this.free_frame = sp_index; //
            return this.free_frame-1; // the position to occupy
        }
        return -1;
    }

    // Deallocation
    public  void  deallocate(int index){
        // call buddy
        this.buddy.deallocate(index);
    }
}

class Frame{

    int[] data;

    // Constructor when receiving data
    public Frame(int[] d) {
        this.data = new int[256];
        for (int i = 0; i < 256; i++) {
            this.data[i] = d[i];
        }
    }

    // Constructor when receiving frame of single page
    public Frame(Frame f) {
        this.data = new int[256];
        System.arraycopy(f.data, 0, this.data, 0, 256);
    }

}

