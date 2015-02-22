# ZDoomResourceRandomizer
Randomizes resources from ZDoom mods

Current Features:
----------------
* Randomizes TEXTURES, ANIMDEFS, SNDINFO, and LANGUAGE files within themselves or with other files
* Automatically creates a pk3

Planned Features:
-----------------
* Ability to add your own entries (custom images for textures and such)
* Ability to randomize music.
* GUI
* Ability to parse through pk3 / wad files for data.
* Other randomization techniques (consistantly replace one entry etc)

Command line options:
---------------------

|Argument | Description|
|---------|------------|
-t file | Textures file to randomize. Multiple entries can be used, but the first one will be used as a base
-a file | Animdefs file to randomize. Multiple entries can be used, but the first one will be used as a base
-s file | Snddefs file to randomize. Multiple entries can be used. This will be output as a combined file
-l file | Language file to randomize. Multiple entries can be used. This will be output as a combined file
--RANDOMIZETICS integer | Specify to randomize tics in animdefs, with the maximum being the integer. Do not pass this or pass 0 for no randomization
--LANGUAGEHEADER string | Language header to use in LANGUAGE. Default is [enu default]
--USESOUNDONCE | Specify to use each sound in SNDDEFS only once. This will probably result in not all of the sounds being randomized
--USEMSGONCE | Specify to use each message in LANGUAGE only once. This will probably result in not all of the messages being randomized

License
-------
This is licensed under the GNU GPL v3
