package com.a.wizardry2.capability;

public interface IMana {
    public float get();
    public void set(float x);
    public void add(float x);
    public float getMax();
    public void addMax(float x);

    public boolean isDirty();

    public void sync(float mana, float maxMana);
}
