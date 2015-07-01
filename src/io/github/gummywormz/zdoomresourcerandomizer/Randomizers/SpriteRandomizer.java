/*
 * Copyright (C) 2015 Paul Alves
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.gummywormz.zdoomresourcerandomizer.Randomizers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.Random;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Paul Alves
 */
public class SpriteRandomizer implements ZDoomRandomizer<File> {

    
    private ArrayList<String> lumpNames;
    private ArrayList<File> filesToProcess;
    
    public SpriteRandomizer()
    {
        lumpNames = new ArrayList<>();
        filesToProcess = new ArrayList<>();
    }
    
    
    
    @Override
    public void processFile(File f) throws IOException {
        filesToProcess.add(f);
        ZipInputStream zI = new ZipInputStream(new FileInputStream(f));
        ZipEntry z = null;
       while( (z = zI.getNextEntry()) != null)
       {
           String path = z.getName();
           
           if(path.toLowerCase().startsWith("sprites") && !z.isDirectory())
           {
               String[] tmp = path.split("(/|\\\\)");
               String name = tmp[tmp.length-1];
               lumpNames.add(name);
               //System.out.println("Added" + name);
           }
           
       }
    }

    @Override
    public void addEntries(List<File> entries) {
        
       for(File f : entries)
       {
        filesToProcess.add(f);
       }
       
    }

    @Override
    public void write(File f) throws IOException {
       
        File output = new File(f.getAbsolutePath() + "/Sprites");
        output.mkdirs();
        Random r = new Random();
        for(File fE : filesToProcess)
        {
            //System.out.println("processing " + fE.getName());
            
            if(fE.getName().endsWith(".pk3"))
            {
                ZipInputStream zI = new ZipInputStream(new FileInputStream(fE));
                ZipEntry z = null;
                Boolean done = false;
                while( ((z = zI.getNextEntry()) != null) && !done ) 
                {
                    
                    int rand = r.nextInt(lumpNames.size());
                   //System.out.println(z.getName());
                   
                   if(z.getName().toLowerCase().startsWith("sprites") && !z.isDirectory()){
                   
                   BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(output.getAbsolutePath() + File.separator + lumpNames.get(rand)));
                   
                   lumpNames.remove(rand);
                   
                   byte[] bytesIn = new byte[1024];
                   int read = 0;
                   while ((read = zI.read(bytesIn)) != -1) 
                   {
                     b.write(bytesIn, 0, read);
                   }
                   b.close();
                     
                   if(lumpNames.size() == 0)
                    {
                        done = true;
                        break;
                    }
                }
                }
                zI.close();
            }
            
            else
            {
                int rand = r.nextInt(lumpNames.size());
                FileInputStream fi = new FileInputStream(fE);
                FileOutputStream fo = new FileOutputStream(output.getAbsolutePath() + File.separator + lumpNames.get(rand));
                
                byte[] bytesIn = new byte[1024];
                int read = 0;
                
                 while((read=fi.read(bytesIn))>0)
                 {
                    fo.write(bytesIn,0,read);
                 }
                 
                 fi.close();
                 fo.close();
                 
            }
            
        }
    }
    
}
