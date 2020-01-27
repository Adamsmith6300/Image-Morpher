package com.example.imagemorpher;

public class Line {

    private CircleArea start;
    private CircleArea end;

    public Line(CircleArea start, CircleArea end) {
        this.start = start;
        this.end = end;
    }
    public Line(CircleArea start) {
        this.start = start;
    }


    @Override
    public String toString(){
        String line = "";
        line+= "Start: "+ this.start.toString()+"\n";
        line+= "End: "+ this.end.toString()+"\n";
        return line;
    }

    public CircleArea getStart() {
        return start;
    }

    public void setStart(CircleArea start) {
        this.start = start;
    }

    public CircleArea getEnd() {
        return end;
    }

    public void setEnd(CircleArea end) {
        this.end = end;
    }
}
