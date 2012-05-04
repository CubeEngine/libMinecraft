package de.cubeisland.libMinecraft.bitmask;

/**
 * Represents a bitmask with 64 bits
 *
 * @author Phillip Schichtel
 */
public class LongBitMask
{
    private long mask;

    public LongBitMask()
    {
        this(0);
    }

    public LongBitMask(long mask)
    {
        this.mask = mask;
    }

    public long get()
    {
        return this.mask;
    }

    public long set(long bits)
    {
        return this.mask |= bits;
    }

    public long unset(long bits)
    {
        return this.mask &= ~bits;
    }

    public long toggle(long bits)
    {
        return this.mask ^= bits;
    }

    public boolean isset(long bits)
    {
        return ((this.mask & bits) == bits);
    }
}
