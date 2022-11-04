package com.a.wizardry2.registry;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.item.ItemManaApple;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemCrystal;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(WizardryROTN.MODID)
@Mod.EventBusSubscriber
public final class Items {

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder(){ return null; }

    public static final Item mana_apple = placeholder();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, "mana_apple", new ItemManaApple());

    }

    // It now makes sense to have the name first, since it's shorter than an entire item declaration.
    public static void registerItem(IForgeRegistry<Item> registry, String name, Item item){
        registerItem(registry, name, item, false);
    }

    // It now makes sense to have the name first, since it's shorter than an entire item declaration.
    public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon){

        item.setRegistryName(WizardryROTN.MODID, name);
        item.setUnlocalizedName(item.getRegistryName().toString());
        registry.register(item);
    }
}
