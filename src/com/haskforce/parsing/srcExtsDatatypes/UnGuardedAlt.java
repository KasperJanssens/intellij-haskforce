package com.haskforce.parsing.srcExtsDatatypes;

/**
 * UnGuardedAlt l (Exp l)
 */
public class UnGuardedAlt extends GuardedAltsTopType {
    public SrcInfoSpan srcInfoSpan;
    public ExpTopType exp;

    @Override
    public String toString() {
        return "UnGuardedAlt{" +
                exp +
                '}';
    }
}
