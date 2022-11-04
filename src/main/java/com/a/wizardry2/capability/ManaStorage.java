package com.a.wizardry2.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import javax.annotation.Nullable;

public class ManaStorage implements IStorage<IMana> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IMana> capability, IMana instance, EnumFacing side) {
        return new NBTTagIntArray(new int[]{ Float.floatToIntBits(instance.get()), Float.floatToIntBits(instance.getMax()) });
    }

    @Override
    public void readNBT(Capability<IMana> capability, IMana instance, EnumFacing side, NBTBase nbt) {
        int[] l = ((NBTTagIntArray) nbt).getIntArray();
        instance.sync(Float.intBitsToFloat(l[0]), Float.intBitsToFloat(l[1]));
    }
}
