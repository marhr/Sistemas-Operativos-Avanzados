
// Mariana Hernandez

package com.company;

import java.io.*;
import java.util.*;

public class Main {

    // Address translation objects
    static List<Integer> lru = new LinkedList<Integer>();
    static List<Integer> lruPT = new LinkedList<Integer>();
    static List<Integer> reservation_list = new LinkedList<Integer>();
    static String file = "Addresses.txt";
    static int address;
    static int p_num; // page number
    static int offset;
    static int f_num; // frame number
    static int value;
    static int physical_address;
    static int tlb_miss = 0;
    static int page_fault = 0;
    static boolean is_full = false;
    static TLB tlb = new TLB();
    static PageTable pt = new PageTable();
    static PhysicalMemory pm = new PhysicalMemory();
    static BackUp bs = new BackUp();
    static int base_pages_counter = 0;

    // This method manages paging using least recently used
    static void paging(){
        try {
            Scanner s = new Scanner(new File(file));
            while (s.hasNextInt()){
                address = s.nextInt();
                // Translation
                address = address % 65536; // page size, for 4KB use 4096
                offset = address % 256;
                p_num = address / 256;
                f_num = -1;
                f_num = tlb.get(p_num);

                // Page fault
                if (f_num == -1){ // is neither in the tlb
                    tlb_miss ++;
                    f_num = pt.get(p_num);
                    if(f_num == -1){ // nor in the page table
                        page_fault ++;
                        System.out.println("Page fault :/");
                        Frame f = new Frame(bs.getData(p_num)); // take it from disk
                        if(is_full == true){ // lru behavior

                            pm.frames[lru.get(0)] = new Frame(f.data); //
                            f_num = lru.get(0);

                            int delete = lruPT.get(0); // index of the least used page
                            pt.table[delete] = new PageTableItem(-1, false); // change mapping in the PT
                            tlb.table.remove(delete); // remove mapping from tlb

                            pt.add(p_num, f_num); // add to page table
                            tlb.put(p_num, f_num); // add to tlb

                            lru.remove(lru.indexOf(f_num));
                            lruPT.remove(lruPT.indexOf(delete));
                            lru.add(f_num);
                            lruPT.add(p_num); // refresh the usage of the page

                            physical_address = f_num * 256 + offset;
                            value = pm.getValue(f_num, offset);
                            System.out.println(String.format("Virtual address (Mem was full): %s Physical address: %s Value: %s", address, physical_address, value));
                            continue;

                        }

                        f_num = pm.addFrame(f);
                        if (pm.free_frame == PhysicalMemory.frames.length) {
                            is_full = true;
                        }

                        // Add translation for frame
                        pt.add(p_num, f_num);
                        tlb.put(p_num, f_num);
                    }
                }

                // lru page table
                // Oldest reference will end up in the bottom of the "stack"
                if (lruPT.contains(lruPT.indexOf(p_num))) {
                    lruPT.remove(lruPT.indexOf(p_num));
                    lruPT.add(p_num);
                }

                else {
                    lruPT.add(p_num);
                }

                // lru frame table
                if (lru.contains(lru.indexOf(f_num))) {
                    lru.remove(lru.indexOf(f_num));
                    lru.add(f_num);
                }

                else {
                    lru.add(f_num);
                }

                physical_address = f_num * 256 + offset;
                value = pm.getValue(f_num, offset);
                System.out.println(String.format("Virtual address: %s Physical address: %s Value: %s", address, physical_address, value));
            }

            System.out.println(String.format("TLB miss: %s, Page Fault: %s", tlb_miss, page_fault));

        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }
    // page size, for 4KB use 4096
    // superpage_size given in number of frames
    static void superpaging(int superpage_size){
        LinkedList<Superpage> superpages = new LinkedList<>(); // contains the created SPs
        try{
            Scanner s = new Scanner(new File(file));
             while (s.hasNextInt()){
                 int[][] superp = new int[2][superpage_size];
                 address = s.nextInt();
                 // Translation
                 address = address % 65536; // memory size
                 offset = address % 256;
                 p_num = address / 256;
                 f_num = -1;
                 f_num = tlb.get(p_num);

                if (f_num == -1){ // the mapping is not on the tlb
                    tlb_miss ++;
                    f_num = pt.get(p_num);
                    if(f_num == -1){ // the mapping is not on the PT
                        page_fault ++;
                        System.out.println("Page fault :/");

                        // Find space for SP usando buddy allocator
                        // are there any contiguous frame to create a SP?
                        int sp_index = pm.reserveSuperpage(superpage_size);
                        if (sp_index != -1){ // SP can be allocated
                            superp[0][0] = p_num;
                            superp[1][0] = f_num;
                            for (int i = 1; i < superpage_size; i++) {
                                if (s.hasNextInt()) {
                                    address = s.nextInt();
                                    // Translation
                                    address = address % 65536; // memory size
                                    offset = address % 256;
                                    superp[0][i] = address / 256; // row 0 is page num
                                    superp[1][i] = -1; // row 1 is frame num
                                    // the rest of the pages don't need f_num
                                }
                            }
                        }
                        else if(is_full == true){ // SP can't be allocated
                               // a SP does not fit but memory is not full either
                            // Preemption
                            // as long as buddy can't allocate, keep taking pages out
                            while (sp_index == -1){

                                //sp_index = pm.reserveSuperpage(superpage_size);
                            //}
                            //if(is_full == true){ // memory is full

                                // Attempt preemption by checking the usage of every frame
                                pm.frames[lru.get(0)]= null;
                                pm.deallocate(lru.get(0));           //SP_INDEX AHORA DEBE DE SER EL INDICE QUE DEVUELVE DEALLOC
                                //f_num = lru.get(0);                // BORRAR EL MAPEO EN PT Y TLB DE LA SP EN EL INDICE DE LRU
                                System.out.println("FLAG");          // DEALLOC DEBE DEVOLVER EL INDICE FINAL DESPUES DE DEALLOCAR
                                int delete = lruPT.get(0); // index of the least used page
                                pt.table[delete] = new PageTableItem(-1, false); // change mapping in the PT
                                tlb.table.remove(delete); // remove mapping from tlb

                                //pt.add(p_num, f_num); // add to page table
                                //tlb.put(p_num, f_num); // add to tlb

                                //lru.remove(lru.indexOf(f_num));
                                //lruPT.remove(lruPT.indexOf(delete));
                                //lru.add(f_num);
                                //lruPT.add(p_num); // refresh the usage of the page

                                //physical_address = f_num * 256 + offset;
                                //value = pm.getValue(f_num, offset);
                                sp_index = pm.reserveSuperpage(superpage_size);
                                System.out.println(String.format("Virtual address (Full memory): %s Physical address: %s Value: %s", address, physical_address, value));
                                //continue;

                            }
                        }
                        // map SP
                        for (int i = 0; i < superpage_size; i++) {
                            Frame f = new Frame(bs.getData(superp[0][i])); // info from disk in new frame
                            superp[1][i] = pm.addFrame(f);
                        }

                        // I think this is useless because we are using buddy allocation
                        if (pm.free_frame == PhysicalMemory.frames.length) {
                            is_full = true;
                        }

                        // Add just one translation for the whole SP
                        // Here adding the translation of the fault page
                        pt.add(superp[0][0], superp[1][0]);
                        tlb.put(superp[0][0], superp[1][0]);
                    }

                }

                // lru page table
                // Oldest reference will end up in the bottom of the "stack"
                if (lruPT.contains(lruPT.indexOf(superp[0][0]))) {
                    lruPT.remove(lruPT.indexOf(superp[0][0]));
                    lruPT.add(superp[0][0]); // the page ends at the top
                }
                else {
                    lruPT.add(superp[0][0]);
                }

                // lru frame table
                if (lru.contains(lru.indexOf(superp[1][0]))) {
                    lru.remove(lru.indexOf(superp[1][0]));
                    lru.add(superp[1][0]);
                }
                else {
                    lru.add(superp[1][0]);
                }

                // actually getting the value of the page
                physical_address = superp[1][0] * 256 + offset;
                value = pm.getValue(superp[1][0], offset);
                System.out.println(String.format("Virtual address: %s Physical address: %s Value: %s", address, physical_address, value));
            }
            System.out.println(String.format("TLB miss: %s, Page Fault: %s", tlb_miss, page_fault));

        }

        catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

    }

    public static void main(String[] args) {

        int selection;
        // Functionality
        System.out.println("********************************************************************");
        System.out.println("¿Como quieres administrar la memoria?");
        System.out.println("1. Paginacion");
        System.out.println("2. Superpaginas");
        Scanner sc=new Scanner(System.in);
        selection = sc.nextInt();

        switch (selection){
            case 1:
                paging();
                break;

            case 2:
                System.out.println("¿Qué tamaño de pagina quieres?");
                selection = sc.nextInt();
                if(selection!=0){
                    superpaging(selection);
                }
                else
                    superpaging(8);
                break;
        }
        //paging();
        // suggested sized for Superpages: 8, 64
         //superpaging(8);
    }
}
