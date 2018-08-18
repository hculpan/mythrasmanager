package org.culpan.mythrasmanager.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;
import java.util.Optional;

public class MythrasCombatModel {
    public ObservableList<MythrasCombatant> combatants = FXCollections.observableArrayList();

    protected int combatRound = 1;

    protected boolean combatStarted = false;

    public void sortByInitiative() {
        combatants.sort(Comparator.comparingInt(MythrasCombatant::getInitiative).reversed());
    }

    public boolean contains(String name) {
        return combatants.filtered( m -> m.name.equals(name)).size() > 0;
    }

    @SuppressWarnings("unused")
    public boolean hasCombatStarted() {
        return combatStarted;
    }

    @SuppressWarnings("unused")
    public int getCombatRound() {
        return combatRound;
    }

    public void reset() {
        combatants.clear();
        combatRound = 1;
        combatStarted = false;
    }

    public void startCombat() {
        nextRound();
        combatStarted = true;
        combatRound = 1;
        if (combatants.size() > 0 && combatants.get(0).currentActionPoints > 0) {
            combatants.get(0).acting = true;
        }
    }

    public void stopCombat() {
        combatStarted = false;
        combatants.forEach(m -> {
            m.nextRound();
        });
    }

    protected int findActingIndex() {
        int result = 0;
        int index = 0;
        for (MythrasCombatant combatant : combatants){
            if (combatant.isActing()) {
                result = index;
                break;
            } else {
                index++;
            }
        }
        return result;
    }

    public MythrasCombatant findFirstThatCanAct() {
        MythrasCombatant result = null;

        Optional<MythrasCombatant> combatant = combatants.stream().filter(m -> m.currentActionPoints > 0).findFirst();
        if (combatant.isPresent()) {
            result = combatant.get();
        }

        return result;
    }

    protected MythrasCombatant firstCombatantToAct() {
        MythrasCombatant result = findFirstThatCanAct();

        if (result != null) {
            result.acting = true;
        }

        return result;
    }

    public MythrasCombatant nextCombatantToAct() {
        if (combatants.size() == 0) return null;

        MythrasCombatant result = null;
        int actingIndex = findActingIndex();
        combatants.get(actingIndex).acting = false;
        for (int i = actingIndex + 1; i < combatants.size(); i++) {
            if (combatants.get(i).currentActionPoints > 0) {
                result = combatants.get(i);
                break;
            }
        }

        if (result == null) {
            result = firstCombatantToAct();
        }

        if (result != null) {
            result.acting = true;
        }

        return result;
    }

    public void nextRound() {
        combatants.forEach(c -> c.nextRound());
        combatRound++;
        firstCombatantToAct();
    }
}
