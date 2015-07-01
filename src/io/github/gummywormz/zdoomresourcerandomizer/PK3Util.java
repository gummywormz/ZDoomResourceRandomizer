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

package io.github.gummywormz.zdoomresourcerandomizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.zeroturnaround.zip.ZipUtil;


/**
 * Makes pk3 files of directories (zips them...) 
 * @author Paul Alves
 */
public class PK3Util {
    
    /**
     * Zips up the given folder's contents
     * @param baseDir The full path to the base directory of files to zip
     * @param fileName The base file name to save it as (without extension)
     * @throws IOException
     */
    public static void zipFolder(File baseDir,String fileName) throws IOException
    {
        
        String name1 = baseDir.getAbsolutePath() + File.separator + fileName + ".pk3";
        String p = "temp" + File.separator;
        String fullpath = name1.replace(p,"");
        
        //System.out.println(fullpath);
        
        ZipUtil.pack(baseDir, new File (fullpath)); //infinite haxzorz
        
     /*   //TODO: add directory support
        
        byte[] buffer = new byte[1024];
        
        File[] list = baseDir.listFiles();
        
        if(list == null)
        {
            throw new java.lang.IllegalArgumentException
            ("Directory passed is not a directory or was empty.");
        }
        
        FileOutputStream fos = new FileOutputStream(baseDir.getAbsolutePath()+ 
                                              File.separator+fileName+".pk3");
    	ZipOutputStream zos = new ZipOutputStream(fos);
        
        for(File f : list)
        {
            final String absolutePath = f.getAbsolutePath();
        
            if(!absolutePath.endsWith(".pk3")){
                
                String entryName = f.getName();
                
                if(f.getAbsolutePath()){}
                
                ZipEntry z = new ZipEntry(entryName);
                zos.putNextEntry(z);
                FileInputStream in = new FileInputStream(absolutePath);
                int len;
                while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
        	in.close();
            }
        }
        
        zos.closeEntry();
        zos.close();*/
        
    }
    
}
