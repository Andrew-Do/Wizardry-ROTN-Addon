package com.a.wizardry2.capability;

public class Mana implements IMana{
    private float mana = 0F;
    private float manaMax = 0F;
    private boolean dirty = false;

    public float get() {
        return mana;
    }
    public float getMax() {
        return manaMax;
    }
    public void set(float x)
    {
        this.mana = x;
        dirty = true;
    }
    public void add(float x)
    {
        this.mana = Math.max(Math.min(this.mana + x, manaMax), 0F);
        dirty = true;
    }

    public void addMax(float x)
    {
        this.manaMax = Math.max(this.manaMax + x, 0F);
        dirty = true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void sync(float mana, float maxMana) {
        this.mana = mana;
        this.manaMax = maxMana;
        dirty = false;
    }
}
