package org.culpan.mythrasmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MythrasCombatant {
    public static class HitLocation {
        private int armorPoints;

        private String range;

        private int hitPoints;

        private int currentHitPoints;

        private String name;

        public int getArmorPoints() {
            return armorPoints;
        }

        public void setArmorPoints(int armorPoints) {
            this.armorPoints = armorPoints;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public int getHitPoints() {
            return hitPoints;
        }

        public void setHitPoints(int hitPoints) {
            this.hitPoints = hitPoints;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCurrentHitPoints() {
            return currentHitPoints;
        }

        public void setCurrentHitPoints(int currentHitPoints) {
            this.currentHitPoints = currentHitPoints;
        }
    }

    public String name;

    public int actionPoints;

    public int currentActionPoints;

    public int initiative;

    public int intelligence;

    public int dexterity;

    public boolean npc = false;

    public boolean acting = false;

    public List<HitLocation> hitLocations = new ArrayList<>();

    public MythrasCombatant() {

    }

    public MythrasCombatant(String name, int actionPoints, int initiative) {
        this.name = name;
        this.actionPoints = actionPoints;
        this.currentActionPoints = actionPoints;
        this.initiative = initiative;
    }

    public MythrasCombatant(String name, int actionPoints, int currentActionPoints, int initiative) {
        this.name = name;
        this.actionPoints = actionPoints;
        this.currentActionPoints = currentActionPoints;
        this.initiative = initiative;
    }

    public void nextRound() {
        currentActionPoints = actionPoints;
        acting = false;
    }

    static public int calculateInitiative(int intelligence, int dexterity) {
        return (int)Math.round(((double)intelligence + (double)dexterity) / 2);
    }

    static public int calculateActionPoints(int intelligence, int dexterity) {
        return (int)Math.ceil((double)(intelligence + dexterity) / 12);
    }

    public void calculateAttributes(int intelligence, int dexterity) {
        initiative = calculateInitiative(intelligence, dexterity);
        actionPoints = calculateActionPoints(intelligence, dexterity);
        currentActionPoints = actionPoints;
    }

    public boolean hasActionPoints() {
        return currentActionPoints > 0;
    }

    public void calculateAttributes() {
        calculateAttributes(this.intelligence, this.dexterity);
    }

    public int getInitiative() {
        return initiative;
    }

    public boolean isActing() {
        return acting;
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", name, initiative);
    }

    @Override
    public Object clone() {
        MythrasCombatant result = new MythrasCombatant();
        result.name = this.name;
        result.intelligence = this.intelligence;
        result.dexterity = this.dexterity;
        result.npc = this.npc;
        result.calculateAttributes();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MythrasCombatant)) return false;
        MythrasCombatant that = (MythrasCombatant) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public void setCurrentActionPoints(int currentActionPoints) {
        this.currentActionPoints = currentActionPoints;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setActing(boolean acting) {
        this.acting = acting;
    }

    public boolean isNpc() {
        return npc;
    }

    public void setNpc(boolean npc) {
        this.npc = npc;
    }
}
