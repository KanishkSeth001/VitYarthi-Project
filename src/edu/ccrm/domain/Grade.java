package edu.ccrm.domain;

public enum Grade {
    S("S", 10.0, 90, 100),
    A("A", 9.0, 80, 89),
    B("B", 8.0, 70, 79),
    C("C", 7.0, 60, 69),
    D("D", 6.0, 50, 59),
    E("E", 5.0, 40, 49),
    F("F", 0.0, 0, 39);
    
    private final String symbol;
    private final double points;
    private final int minMarks;
    private final int maxMarks;
    
    Grade(String symbol, double points, int minMarks, int maxMarks) {
        this.symbol = symbol;
        this.points = points;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
    }
    
    public static Grade fromMarks(double marks) {
        for (Grade grade : values()) {
            if (marks >= grade.minMarks && marks <= grade.maxMarks) {
                return grade;
            }
        }
        return F;
    }
    
    public String getSymbol() { return symbol; }
    public double getPoints() { return points; }
    public int getMinMarks() { return minMarks; }
    public int getMaxMarks() { return maxMarks; }
    
    @Override
    public String toString() {
        return symbol;
    }
}