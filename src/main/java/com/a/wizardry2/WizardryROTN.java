package com.a.wizardry2;

import com.a.wizardry2.Packet.PacketHandler;
import com.a.wizardry2.main.CommonProxy;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.util.WandHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = WizardryROTN.MODID, name = WizardryROTN.NAME, version = WizardryROTN.VERSION, dependencies = "required-after:ebwizardry@[4.3.0,4.4)")
public class WizardryROTN
{
    public static final String MODID = "wizardry2";
    public static final String NAME = "Wizardry - ROTN Additions";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.a.wizardry2.client.ClientProxy", serverSide = "com.a.wizardry2.main.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(WizardryROTN.MODID)
    public static WizardryROTN instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preinit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(instance);

        proxy.init();
        PacketHandler.initPackets();

    }

    public static final String[] problemMods = new String[]{"mantle"};

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }
}




//TODO: Wand Spell System
    //TODO: open menu to memorize spells aka, add spells to inventory, refer to baubles source code
        //press button
        //if no wand in main hand, then menu wont open
    //TODO: toolbelt spell selection system
        //if no wand in main hand, then menu wont open
    //TODO: Disable wizardry client spell switch crouching and controls
    //TODO: Display all spells in gui like mmo

    //Spellchain
    //TODO: Define battle casting in wand, what slot to switch to after casting x spell
    //TODO: Casting spell sounds - hear whispers

//TODO: Curate Spells and schools
    //Varying Spells?
    //TODO: Attach extra properties to spellbooks
    //Research System?
    //TODO: Change Spell Colors
    //TODO: Remove Spell Loot and stuff

    //TODO: Spell cast times and cooldowns
        //Ultimate Spells have long cooldown and cast time


