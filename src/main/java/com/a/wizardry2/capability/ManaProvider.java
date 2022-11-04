package com.a.wizardry2.capability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ManaProvider implements ICapabilitySerializable<NBTBase>
{
    @CapabilityInject(IMana.class)
    public static final Capability<IMana> MANA_CAPABILITY = null;

    private IMana instance = MANA_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == MANA_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == MANA_CAPABILITY ? MANA_CAPABILITY.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT()
    {
        return MANA_CAPABILITY.getStorage().writeNBT(MANA_CAPABILITY, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        MANA_CAPABILITY.getStorage().readNBT(MANA_CAPABILITY, this.instance, null, nbt);
    }
}
