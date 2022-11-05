package com.a.wizardry2.mixin.wizardry;

import electroblob.wizardry.Settings;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.spell.Spell;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.util.*;

@Mixin(Settings.class)
public abstract class mSettings {

    Set<String> allowedSpells = new HashSet<String>();

    @Overwrite(remap = false)
    private void setupSpellsConfig(){
        getAllowedSpells();

        for(Spell spell : Spell.getAllSpells()){
            String spell_name = spell.getUnlocalisedName().substring(spell.getUnlocalisedName().indexOf(':') + 1);
            boolean test = allowedSpells.contains(spell_name);
            spell.setEnabled(allowedSpells.contains(spell_name));
        }
    }

    private void getAllowedSpells()
    {
        File spellJSONDir = new File(Wizardry.configDirectory, "spells");

        //TODO: Dump spells in if spell directory doesn't exist
        if(spellJSONDir.exists())
        {
            for(File file : FileUtils.listFiles(spellJSONDir, new String[]{"json"}, true)){
                // The structure in world and config folders is subtly different in that the "spells" and mod id directories
                // are in the opposite order, i.e. it's spells/modid/whatever.json instead of modid/spells/whatever.json
                String relative = spellJSONDir.toPath().relativize(file.toPath()).toString(); // modid\whatever.json
                String nameAndModID = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/"); // modid/whatever
                String name = nameAndModID.substring(nameAndModID.indexOf('/') + 1); // whatever
                allowedSpells.add(name);
            }
        }
        else
        {

        }
    }

    @Shadow
    private Configuration config;

    @Shadow
    abstract void checkForRedundantOptions(String categoryName, Collection<String> validKeys);



}
