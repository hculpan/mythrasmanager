package org.culpan.mythrasmanager.model;

import javafx.beans.property.IntegerProperty;

import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TemplateEntry {
    public enum PatternFlags { CASE_SENSITIVE, WHOLE_WORD, START_WORD, RABBLE, NOVICE, SKILLED, VETERAN, MASTER }

    protected String name;

    protected List<String> tags;

    protected Integer rank;

    protected String race;

    protected String owner;

    protected Long id;

    private static Pattern pattern;

    private static EnumSet<PatternFlags> lastFlags = EnumSet.noneOf(PatternFlags.class);

    public static boolean setPattern(String filter, EnumSet<PatternFlags> patternFlags) {
        lastFlags = patternFlags;

        try {
            if (patternFlags.contains(PatternFlags.WHOLE_WORD)) filter = "\\b" + filter + "\\b";
            if (patternFlags.contains(PatternFlags.START_WORD)) filter = "^" + filter;

            if (patternFlags.contains(PatternFlags.CASE_SENSITIVE)) {
                pattern = Pattern.compile(filter);
            } else {
                pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
            }
        } catch (PatternSyntaxException e) {
            return false;
        }

        return true;
    }

    public boolean matches(String filter) {
        if (lastFlags.contains(PatternFlags.RABBLE) ||
            lastFlags.contains(PatternFlags.NOVICE) ||
            lastFlags.contains(PatternFlags.SKILLED) ||
            lastFlags.contains(PatternFlags.VETERAN) ||
            lastFlags.contains(PatternFlags.MASTER)) {
            if (rank == 1 && !lastFlags.contains(PatternFlags.RABBLE) && rank != 1) return false;
            if (rank == 2 && !lastFlags.contains(PatternFlags.NOVICE) && rank != 1) return false;
            if (rank == 3 && !lastFlags.contains(PatternFlags.SKILLED) && rank != 1) return false;
            if (rank == 4 && !lastFlags.contains(PatternFlags.VETERAN) && rank != 1) return false;
            if (rank == 5 && !lastFlags.contains(PatternFlags.MASTER) && rank != 1) return false;
        }

        try {
            Matcher matcher = pattern.matcher(name);
            return matcher.find();
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
