package com.a.wizardry2.registry;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.potion.PotionBurnLife;
import com.a.wizardry2.potion.PotionRestoreMana;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(WizardryROTN.MODID)
@Mod.EventBusSubscriber
public class Potions
{

    private Potions(){} // No instances!
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder(){ return null; }

    public static final Potion burn_life = placeholder();
    public static final Potion restore_mana = placeholder();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Potion> event)
    {
        IForgeRegistry<Potion> registry = event.getRegistry();
        registerPotion(registry, "burn_life", new PotionBurnLife(true, 0x38ddec, null));
        registerPotion(registry, "restore_mana", new PotionRestoreMana(true, 0x38ddec, null));
    }

    /**
     * Sets both the registry and unlocalised names of the given potion, then registers it with the given registry. Use
     * this instead of {@link Potion#setRegistryName(String)} and {@link Potion#setPotionName(String)} during
     * construction, for convenience and consistency.
     *
     * @param registry The registry to register the given potion to.
     * @param name The name of the potion, without the mod ID or the .name stuff. The registry name will be
     *        {@code ebwizardry:[name]}. The unlocalised name will be {@code potion.ebwizardry:[name].name}.
     * @param potion The potion to register.
     */
    public static void registerPotion(IForgeRegistry<Potion> registry, String name, Potion potion)
    {
        potion.setRegistryName(WizardryROTN.MODID, name);
        // For some reason, Potion#getName() doesn't prepend "potion." itself, so it has to be done here.
        potion.setPotionName("potion." + potion.getRegistryName().toString());
        registry.register(potion);
    }

}
