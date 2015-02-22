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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Paul Alves
 */
public class LanguageRandomizer implements ZDoomRandomizer<String>{
    
    private ArrayList<String> names;
    private ArrayList<String> messages;
    private String lang = "[enu default]";
    private final boolean removeMsg;
    
    /**
     * Creates a new language randomizer
     * @param language The language header to use, pass null for english
     * @param removeFlag Whether or not to remove messages from the list when 
     * replaced
     */
    public LanguageRandomizer(String language, boolean removeFlag)
    {
        names = new ArrayList<>();
        messages = new ArrayList<>();
        if(language != null){lang = language;}
        removeMsg = removeFlag;
    }

    /**
     * Processes the given language file
     * @param f The file to parse
     * @throws IOException
     */
    @Override
    public void processFile(File f) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = "";
        boolean isComment = false; //tracks multiline comments
        while( (line = in.readLine()) != null)
        {

            if(line.startsWith("/*") && !isComment){isComment = true;}
            if(isComment && line.endsWith("*/"))
            {
                isComment = false;
                continue;
            }
            //skip unsupported lines / whitespace
            if(isComment || line.startsWith("//") 
            || line.trim().isEmpty()|| line.startsWith("[")){continue;}

            String[] data = line.split("=");
            names.add(data[0].trim());
            messages.add(data[1].trim());
            
        }
        in.close();
    }

    /**
     * Adds additional messages to this randomizer
     * @param entries messages to add
     */
    @Override
    public void addEntries(List<String> entries) {
        for(String e : entries)
        {
            messages.add(e);
        }
    }

    /**
     * Outputs the randomized file
     * @param f The new file to output
     * @throws IOException
     */
    @Override
    public void write(File f) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        String sep = System.lineSeparator();

        Random r = new Random();

        ArrayList<String> noDupNames = new ArrayList<>
            (new LinkedHashSet<>(names));

        ArrayList<String> noDupMsg = new ArrayList<>
            (new LinkedHashSet<>(messages));

        out.write(lang + sep + sep);
        
        for(String name : noDupNames)
        {
            if(noDupMsg.size() < 1){break;}//not all entries were modified!
            int entryNum = r.nextInt(noDupMsg.size());
            String entry = noDupMsg.get(entryNum).trim();
            String randomizedEntry = name +  " = " + entry;
            out.write(randomizedEntry + sep);
            if(removeMsg){noDupMsg.remove(entryNum);}

        }
        
        out.close();
    }
    
}
