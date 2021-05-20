package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterUtill {
    public static void writeToFile(String fileName, String content) throws IOException{
        File file = new File(fileName);

        if (file.createNewFile()) {
        	FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.newLine();
            bw.close();
            System.out.println(fileName+" is created!\n");
        } else {
            System.out.println(fileName+" already exists.\n");
        }
        
    }
}
