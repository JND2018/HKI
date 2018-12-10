package de.jnd;

import java.util.Arrays;
import java.util.stream.Collectors;


public class App 
{
    public static void main( String[] args )
    {
        Arrays.stream(args).filter(c -> c.startsWith("-")).collect(Collectors.toList()).forEach(System.out::println);

    }
}
