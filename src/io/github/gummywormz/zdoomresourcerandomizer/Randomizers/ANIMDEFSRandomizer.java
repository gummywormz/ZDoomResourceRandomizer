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
 * Randomizes ANIMDEFS within itself or with other files
 * @author Paul Alves
 */
public class ANIMDEFSRandomizer implements ZDoomRandomizer<String>{
    
    private final ArrayList<String> patches;
    private final File base;
    private final int randomizeTics;

    /**
     * Creates a new ANIMDEFS randomizer
     * @param baseFile The file to base the randomizer on. 
     * (The file with the textures you want to replace)
     * This is usually the main TEXTURES file for the game / mod.
     * This file is not processed, so you will have to call 
     * {@link #processFile(java.io.File) } manually
     * @param ticRandom Whether to randomize the tic amounts as well 
     * (0 means off, anything else is the maximum tics to be generated)
     */
    public ANIMDEFSRandomizer(File baseFile,int ticRandom)
    {
        base = baseFile;
        randomizeTics = ticRandom;
        patches = new ArrayList<>();
    }
    
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
            || line.trim().isEmpty()){continue;}

            line = line.trim();
            
            String lineLower = line.toLowerCase();

            if(lineLower.startsWith("pic"))
            {
                String[] entry = line.split(" ");
                String patchName = entry[1];
                patches.add(patchName);
            }
        }
        in.close();
    }

    @Override
    public void addEntries(List<String> entries) {
        for(String e : entries)
        {
            patches.add(e);
        }
    }

    @Override
    public void write(File output) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(base));
        String line = "";
        Random r = new Random();
        BufferedWriter out = new BufferedWriter(new FileWriter(output));
        String sep = System.lineSeparator();

        ArrayList<String> noDupPatches = new ArrayList<>
            (new LinkedHashSet<>(patches));

        while( (line = in.readLine()) != null)
        {
            if(line.toLowerCase().trim().contains("pic"))
            {
                int tic = Integer.parseInt(line.trim().split(" ")[3]);
                int entryNum = r.nextInt(noDupPatches.size());
                String entry = noDupPatches.get(entryNum);
                if(randomizeTics > 0){tic = r.nextInt(randomizeTics);}
                line = "pic " + entry + " tics " + tic;
            }

            out.write(line + sep);

        }

        in.close();
        out.close();
    }
    
}
