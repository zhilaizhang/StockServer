package com.zlzhang.hello;

import java.io.*;

public class DataParse {
    public static void main(String[] args){
        File file =new File("/Users/zhangzhilai/Desktop/caishi/3.26/userInfo.txt");
        System.out.println(readFile(file));
    }

    public static String readFile(File file) {
        StringBuilder result =new StringBuilder();
        try {
            BufferedReader br =new BufferedReader(new FileReader(file));
            String s =null;
            while((s =br.readLine()) != null) { //一次读一行内容
                result.append(System.lineSeparator() +s);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    public  static synchronized void writeToFile(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
