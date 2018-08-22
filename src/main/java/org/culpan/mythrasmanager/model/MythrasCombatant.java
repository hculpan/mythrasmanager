package org.culpan.mythrasmanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MythrasCombatant {
    public static class HitLocation {
        public enum WoundLevel { None, Minor, Serious, Major }

        private int armorPoints;

        private String range;

        private int hitPoints;

        private int currentHitPoints;

        private String name;

        private String wound = "";

        private String effect = "";

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

        public String getWound() {
            return wound;
        }

        public void setWound(String wound) {
            this.wound = wound;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public void calculateWound() {
            if (getHitPoints() == getCurrentHitPoints()) {
                wound = "";
                effect = "";
            } else if (getCurrentHitPoints() > 0) {
                wound = "minor";
                effect = "";
            } else if (getCurrentHitPoints() > getHitPoints() * -1) {
                wound = "serious";
                if (name.contains("arm") || name.contains("leg")) {
                    effect = "Endurance test or becomes useless";
                } else {
                    effect = "Endurance test or lose consciousness";
                }
            } else {
                wound = "major";
                if (name.contains("arm") || name.contains("leg")) {
                    effect = "Limb is severed or shattered; drop prone and make Endurance to stay conscious";
                } else {
                    effect = "Unconscious; make Endurance test or immediately die";
                }
            }
        }
    }

    public String name;

    public int actionPoints;

    public int currentActionPoints;

    public int initiative;

    public int armorPenalty;

    public int currentInitiative;

    public int intelligence;

    public int dexterity;

    public boolean npc = false;

    public boolean acting = false;

    private String rawJsonFile;

    public List<HitLocation> hitLocations = new ArrayList<>();

    public MythrasCombatant() {

    }

    public MythrasCombatant(String name, int actionPoints, int initiative) {
        this.name = name;
        this.actionPoints = actionPoints;
        this.currentActionPoints = actionPoints;
        this.initiative = initiative;
        this.currentInitiative = initiative;
    }

    public MythrasCombatant(String name, int actionPoints, int currentActionPoints, int initiative) {
        this.name = name;
        this.actionPoints = actionPoints;
        this.currentActionPoints = currentActionPoints;
        this.initiative = initiative;
        this.currentInitiative = initiative;
    }

    public void nextRound() {
        currentActionPoints = actionPoints;
        acting = false;
    }

    static public int calculateInitiative(int intelligence, int dexterity, int armorPenalty) {
        return (int)Math.round(((double)intelligence + (double)dexterity) / 2) - armorPenalty;
    }

    static public int calculateActionPoints(int intelligence, int dexterity) {
        return (int)Math.ceil((double)(intelligence + dexterity) / 12);
    }

    public void calculateAttributes(int intelligence, int dexterity) {
        initiative = calculateInitiative(intelligence, dexterity, armorPenalty);
        currentInitiative = initiative;
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
        result.armorPenalty = this.armorPenalty;
        result.rawJsonFile = this.rawJsonFile;
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
        this.currentInitiative = initiative;
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

    public int getCurrentInitiative() {
        return currentInitiative;
    }

    public void setCurrentInitiative(int currentInitiative) {
        this.currentInitiative = currentInitiative;
    }

    public int getArmorPenalty() {
        return armorPenalty;
    }

    public void setArmorPenalty(int armorPenalty) {
        this.armorPenalty = armorPenalty;
    }

    public String getRawJsonFile() {
        return rawJsonFile;
    }

    public void setRawJsonFile(String rawJsonFile) {
        this.rawJsonFile = rawJsonFile;
    }
}
