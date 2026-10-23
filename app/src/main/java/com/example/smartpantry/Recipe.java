package com.example.smartpantry;
import java.util.*;
public class Recipe {
    public static class Ing { public String name, unit; public double qty; }
    public long id; public String name, steps;
    public List<Ing> ings = new ArrayList<>();
}
