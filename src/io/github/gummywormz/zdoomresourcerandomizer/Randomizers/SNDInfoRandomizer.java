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
import java.util.regex.Pattern;
import java.util.Random;

/**
 * Randomizes SNDInfo either within itself or with other files
 * @author Paul Alves
 */
public class SNDInfoRandomizer implements ZDoomRandomizer<String> {

    /**
     * Regex / Pattern for matching $playersound entries
     */
    static final Pattern PAT_PLAYERSND = Pattern.compile
        ("(?<=\\*(death|xdeath|jump|gibbed|pain(\\d{2,3}+|-)|grunt|land|fist|"
            + "usefail|taunt))");

    /**
     * Regex / Pattern for matching any other entries
     */
    static final Pattern PAT_GENERAL = Pattern.compile
        ("\\s+");//plus is needed to match multiple whitespace chars

    private final ArrayList<String> names;
    private final ArrayList<String> values;
    private final ArrayList<String> extra;
    private boolean removeValues = false;
    private int lineNum;

    /**
     * Creates new SNDInfoRandomizer
     * @param valuesFlag Whether to remove values after using them
     */
    public SNDInfoRandomizer(boolean valuesFlag)
    {
        names = new ArrayList<>();
        values = new ArrayList<>();
        extra = new ArrayList<>();
        removeValues = valuesFlag;
        lineNum = 0;
    }

    /**
     * Processes the given SNDINFO file and adds the sounds to the Randomizer
     * @param f The file to process
     * @throws java.io.FileNotFoundException
     */
    @Override
    public void processFile(File f) throws IOException
    {
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line = "";
        boolean isComment = false; //tracks multiline comments
        while( (line = in.readLine()) != null)
        {
            
            lineNum++;
            
            if(line.startsWith("/*") && !isComment){isComment = true;}

            if(isComment && line.endsWith("*/"))
            {
                isComment = false;
                continue;
            }

            //skip unsupported lines / whitespace
            if(isComment || line.startsWith("//") 
                    || line.trim().isEmpty()){continue;}

            if(line.toLowerCase().startsWith("$playersound"))
            {
                String[] snd = PAT_PLAYERSND.split(line);
                String nameS = snd[0];
                String value = snd[1];
                
                //hacktastic check for damagetypes 
                //(can't easily shove it into the regex)
                if(nameS.endsWith("-") || value.startsWith("-"))
                {
                    String[] value2 = PAT_GENERAL.split(value.trim());
                    nameS = nameS + value2[0];
                    value = value2[1];
                }
                
                names.add(nameS);
                values.add(value);
                continue;
            }

            if(!line.startsWith("$"))
            {
                String[] snd = PAT_GENERAL.split(line);
                names.add(snd[0]);
                values.add(snd[1]);
            }
            else
            {
                extra.add(line+"@"+lineNum);
            }

        }
        in.close();

    }

    /**
     * Adds the given entries to the randomizer
     * NOTE: it is assumed all entry names are <= 8 characters
     * @param entries List of entries to process.
     */
    @Override
    public void addEntries(List<String> entries)
    {
        for(String e : entries)
        {
            values.add(e);
        }
    }

    /**
     * Outputs the sndinfo file
     * @param f file to output
     * @throws IOException 
     */
    @Override
    public void write(File f) throws IOException
    {

        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        String sep = System.lineSeparator();

        Random r = new Random();

        ArrayList<String> noDupNames = new ArrayList<String>
            (new LinkedHashSet<String>(names));

        ArrayList<String> noDupValues = new ArrayList<String>
            (new LinkedHashSet<String>(values));
        
        int extraIndex = 0;//since the lines are added in increasing order,
        //we can assume that the indexes correspond to increase line numbers
        
        int lineNumX = 1; //we don't use i since it might not be the same

        for(int i = 0; i <noDupNames.size(); i++)
        {
            String name = noDupNames.get(i);
            
            //places extra lines if needed
            if(extraIndex < extra.size())
            {
                String extraLines = extra.get(extraIndex);
                String[] lineContent = extraLines.split("@");
                int insertLine = Integer.parseInt(lineContent[1]);
                
                if(lineNumX == insertLine)
                {
                out.write(lineContent[0] + sep);
                extraIndex++;
                }
            }
            
            if(noDupValues.size() < 1){break;}//not all entries were modified!
            int entryNum = r.nextInt(noDupValues.size());
            String entry = noDupValues.get(entryNum).trim();
            String randomizedEntry = name +  " " + entry;
            out.write(randomizedEntry + sep);
            lineNumX++;
            if(removeValues){noDupValues.remove(entryNum);}

        }
        
        out.close();

    }

}
