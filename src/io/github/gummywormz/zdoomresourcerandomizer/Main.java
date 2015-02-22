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

import io.github.gummywormz.zdoomresourcerandomizer.Randomizers.ANIMDEFSRandomizer;
import io.github.gummywormz.zdoomresourcerandomizer.Randomizers.LanguageRandomizer;
import io.github.gummywormz.zdoomresourcerandomizer.Randomizers.SNDInfoRandomizer;
import io.github.gummywormz.zdoomresourcerandomizer.Randomizers.TEXTURESRandomizer;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Command line interface that randomizes the given files only. Really limited
 * at the moment
 * @author Paul Alves
 */
public class Main {
    
    public static void main(String[] args)
    {
        if(args.length < 2)
        {
            usage();
        }
        
        ArrayList<File> textures = new ArrayList<>();
        ArrayList<File> sndinfo = new ArrayList<>();
        ArrayList<File> animdefs = new ArrayList<>();
        ArrayList<File> language = new ArrayList<>();
        
        boolean removeSnd = false;
        boolean removeMsg = false;
        int ticLimit = 0;
        String langHead = null;
        
        for(int i = 0; i < args.length; i++)
        {
            if(args[i].equals("-t"))
            {
                File f = new File(args[i+1]);
                textures.add(f);
            }
            
            if(args[i].equals("-a"))
            {
                File f = new File(args[i+1]);
                animdefs.add(f);
            }
            
            if(args[i].equals("-s"))
            {
                File f = new File(args[i+1]);
                sndinfo.add(f);
            }
            
            if(args[i].equals("-l"))
            {
                File f = new File(args[i+1]);
                System.out.println(f.getAbsolutePath());
                language.add(f);
            }
            
            if(args[i].equals("--RANDOMIZETICS"))
            {
                ticLimit = Integer.parseInt(args[i+1]);
            }
            
            if(args[i].equals("--LANGUAGEHEADER"))
            {
                langHead = args[i+1];
            }
            
            if(args[i].equals("--USESOUNDSONCE"))
            {
                removeSnd = true;
            }
            
            if(args[i].equals("--USEMSGSONCE"))
            {
                removeMsg = true;
            }
        }
        
        if(textures.isEmpty() && animdefs.isEmpty() && sndinfo.isEmpty()
                && language.isEmpty())
        {
            System.out.println("No files were given to be randomized!");
            System.exit(2);
        }
        
        new File("temp").mkdir();
        
        try{
            
        if(!textures.isEmpty())
        {
            System.out.println("Randomizing given TEXTURES files...");
            File base = textures.get(0);
            TEXTURESRandomizer t = new TEXTURESRandomizer(base);
            t.processFile(base);
           
            for(int i = 1; i < textures.size(); i++)
            {
                base = textures.get(i);
                t.processFile(base);
            }
            System.out.println("Writing new file");
            t.write(new File("temp/TEXTURES.random"));
        }
        
        if(!sndinfo.isEmpty())
        {
            System.out.println("Randomizing given SNDINFO files...");
            SNDInfoRandomizer s = new SNDInfoRandomizer(removeSnd);
            
            for(File f : sndinfo)
            {
                s.processFile(f);
            }
            System.out.println("Writing new file");
            s.write(new File("temp/SNDINFO.random"));
        }
        
        
        if(!animdefs.isEmpty())
        {
            System.out.println("Randomizing given ANIMDEFS files...");
            File base = animdefs.get(0);
            ANIMDEFSRandomizer t = new ANIMDEFSRandomizer(base,ticLimit);
            t.processFile(base);
           
            for(int i = 1; i < animdefs.size(); i++)
            {
                base = animdefs.get(i);
                t.processFile(base);
            }
            System.out.println("Writing new file");
            t.write(new File("temp/ANIMDEFS.random"));
        }
        
        if(!language.isEmpty())
        {
            System.out.println("Randomizing given LANGUAGE files...");
            LanguageRandomizer l = new LanguageRandomizer(langHead,removeMsg);
            
            for(File f : language)
            {
                l.processFile(f);
            }
            System.out.println("Writing new file");
            l.write(new File("temp/LANGUAGE.random"));
        }
        
        System.out.println("Making PK3...");
        
        String date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss")
                .format(new Date());
        
        PK3Util.zipFolder(new File("temp"), "randomized" + date);
        
        }
        catch(IOException ex)
        {
            System.out.println("IO Error! " + ex.getMessage());
        }
        
        System.out.println("Done!");
        
    }

    private static void usage() {
        System.out.println("ZDoom Resource Randomizer");
        System.out.println("https://github.com/gummywormz/ZDoomResourceRandomizer");
        System.out.println("-----------------------");
        System.out.println("Parameters:");
        System.out.println("-t file | Textures file to randomize."
                + " Multiple entries can be used, "
                + "but the first one will be used as a base");
        System.out.println("-a file | Animdefs file to randomize."
                + " Multiple entries can be used, "
                + "but the first one will be used as a base");
        
        System.out.println("-s file | Snddefs file to randomize. "
                + " Multiple entries can be used. "
                + "This will be output as a combined file");
        
        System.out.println("-l file | Language file to randomize."
                + " Multiple entries can be used. "
                + "This will be output as a combined file");
        
        System.out.println("--RANDOMIZETICS integer | Specify to randomize"
                + " tics in animdefs, with the maximum being the integer. "
                + "Do not pass this or pass 0 for no randomization");
        
        System.out.println("--LANGUAGEHEADER string | Language header to use"
                + " in LANGUAGE. Default is [enu default]");
        
        System.out.println("--USESOUNDONCE | Specify to use each sound in "
                + " SNDDEFS only once. This will probably result in not all"
                + " of the sounds being randomized");
        
        System.out.println("--USEMSGONCE | Specify to use each message in "
                + " LANGUAGE only once. This will probably result in not all"
                + " of the messages being randomized");
        
        System.exit(2);
        
    }
    
}
