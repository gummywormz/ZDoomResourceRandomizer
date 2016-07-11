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
import java.util.regex.Matcher;
import java.util.Random;

/**
 * Randomizes TEXTURES (ZDoom format) within itself or with other files
 * @author Paul Alves
 */
public class TEXTURESRandomizer implements ZDoomRandomizer<String>{

    /**
     * Pattern to remove all text before the patch name
     */
    static final Pattern PAT_PATCH_PREFIXDEL = Pattern.compile(".+patch ",
            Pattern.CASE_INSENSITIVE);

    /**
     * Pattern to remove all text after the patch name
     */
    static final Pattern PAT_PATCH_SUFFIXDEL = Pattern.compile(",.+");

    /**
     * Pattern to allow replacement of the patch name
     */
    static final Pattern PAT_PATCH_REPLACEPATCHNAME = 
        Pattern.compile("patch (\\w|\\d){1,8},",Pattern.CASE_INSENSITIVE);

    private final ArrayList<String> patches;
    private final File base;
    
    /**
     * Constructs a new TEXTURES Randomizer
     * @param baseFile The file to base the randomizer on. 
     * (The file with the textures you want to replace)
     * This is usually the main TEXTURES file for the game / mod.
     * This file is not processed, so you will have to call 
     * {@link #processFile(java.io.File) } manually
     */
    public TEXTURESRandomizer(File baseFile)
    {
        patches = new ArrayList<>();
        base = baseFile;
    }

    /**
     * Processes a TEXTURES file to add patch names to the randomizer
     * @param f The file to process
     * @throws IOException
     */
    @Override
    public void processFile(File f) throws IOException 
    {
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

            String lineLower = line.toLowerCase();

            if(lineLower.contains("patch"))
            {
                Matcher prefix = PAT_PATCH_PREFIXDEL.matcher(line);
                String patchPt1 = prefix.replaceAll("");

                Matcher suffix = PAT_PATCH_SUFFIXDEL.matcher(patchPt1);
                String patchName = suffix.replaceAll("").trim();

                patches.add(patchName);

            }
        }
        in.close();
    }

    /**
     * Adds the given entries to the randomizer
     * NOTE: it is assumed all entry names are <= 8 characters
     * @param entries List of entries to add
     */
    @Override
    public void addEntries(List<String> entries) {
        for(String e : entries)
        {
            patches.add(e);
        }
    }

    /**
     * Outputs the randomized TEXTURES file
     * @param output The name of the new file to write to
     * @throws IOException
     */
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
            if(line.toLowerCase().contains("patch"))
            {
                Matcher patchReplace = PAT_PATCH_REPLACEPATCHNAME.matcher(line);
                int entry = r.nextInt(noDupPatches.size());
                String patch = noDupPatches.get(entry);
                line = patchReplace.replaceAll("Patch " + patch + ", ");
            }

            out.write(line + sep);

        }

        in.close();
        out.close();

    }

}
