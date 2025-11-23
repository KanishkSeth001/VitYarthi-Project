package edu.ccrm.domain;

public enum Semester {
    SPRING("Spring", 1),
    SUMMER("Summer", 2),
    FALL("Fall", 3);
    
    private final String displayName;
    private final int order;
    
    Semester(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }
    
    public String getDisplayName() { return displayName; }
    public int getOrder() { return order; }
    
    @Override
    public String toString() {
        return displayName;
    }
}