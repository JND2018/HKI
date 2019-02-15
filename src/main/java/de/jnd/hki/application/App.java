package de.jnd.hki.application;

import de.jnd.hki.testCV;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;

public class App {


    public static void main(String[] args) {
        //nu.pattern.OpenCV.loadShared();
        Loader.load(opencv_java.class);
        //testCV.testAffineTransformation();
        //testCV.detectEdges(testCV.imgImport());
        //testCV.imgExport(testCV.imgImport(), "testkreuzbin.png");
        //testCV.imgExport(testCV.detectCross(testCV.imgImport()), "detectcross.png");
        //testCV.imgExport(testCV.detectCrossCascade(testCV.imgImport()), "detectcross.png");
        testCV.imgExport(testCV.detectAruCo(testCV.imgImport()), "detectaruco.png");
    }
}
