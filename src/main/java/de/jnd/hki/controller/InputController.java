package de.jnd.hki.controller;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class NoAruComarkersFoundException extends Exception {
     NoAruComarkersFoundException() {
        super();
    }
}

class TooFewAruComarkersFoundException extends Exception {
    TooFewAruComarkersFoundException() {
        super();
    }
}

public class InputController {
    private static Logger log = LoggerFactory.getLogger(InputController.class);

    public static Mat openImg(String path) throws IOException {
        log.info("Loading image " + path);
        Mat dst = Imgcodecs.imread(path, Imgcodecs.IMREAD_GRAYSCALE);
        if (dst.empty()) {
            throw new IOException();
        }
        return dst;
    }

    public static Mat preprocessImg(Mat src) {
        Mat dst = new Mat();
        Imgproc.threshold(src, dst, 170, 255, Imgproc.THRESH_BINARY);
        log.info("Preprocessing image...");
        return dst;
    }

    public static List<Point> detectAruCo(Mat src) throws NoAruComarkersFoundException {
        List<Mat> markers = new ArrayList<>();
        Mat ids = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
        Aruco.detectMarkers(src, dictionary, markers, ids);
        List<Point> centers = new ArrayList<>();
        for (Mat marker: markers) {
            Point a = new Point(marker.get(0,0));
            Point b = new Point(marker.get(0,1));
            Point c = new Point(marker.get(0,2));
            Point d = new Point(marker.get(0,3));
            double x = 0.25 * (a.x + b.x + c.x + d.x);
            double y = 0.25 * (a.y + b.y + c.y + d.y);
            centers.add(new Point(x,y));
        }
        if (centers.size() <= 0) {
            throw new NoAruComarkersFoundException();
        }
        log.info(String.format("Detected %s AruCo Markers", centers.size()));
        return centers;
    }

    public static Mat affineTransform(Mat src, List<Point> corners) throws TooFewAruComarkersFoundException{
        if (corners.size() < 4) {
            throw new TooFewAruComarkersFoundException();
        }
        MatOfPoint2f srcMat = new MatOfPoint2f(
                corners.get(2),
                corners.get(3),
                corners.get(1)
        );
        MatOfPoint2f dstMat = new MatOfPoint2f(
                new Point(0,0),
                new Point(src.cols()-1,0),
                new Point(0, src.rows()-1)
        );
        Mat transformMatrix = Imgproc.getAffineTransform(srcMat, dstMat);
        Mat dst = new Mat();
        log.info("Warping image...");
        Imgproc.warpAffine(src, dst, transformMatrix, new Size(src.cols(), src.rows()));
        log.info("Inverting image...");
        Imgproc.threshold(dst, dst, 0, 255, Imgproc.THRESH_BINARY_INV);
        return dst;
    }

    public static List<Mat> cutLetters(Mat src) {
        return cutLetters(src, 14, 22, 1.5f, 28);
    }

    public static List<Mat> cutLetters(Mat src, int cellCols, int cellRows, float cellOffset, int outputSize) {
        List<Mat> dst = new ArrayList<>();

        float cellSizeRows = (float)src.rows() / (cellRows + (cellOffset * 2));
        float cellSizeCols = (float)src.cols() / (cellCols + (cellOffset * 2));
        int offsetRows = Math.round(cellOffset * cellSizeRows);
        int offsetCols = Math.round(cellOffset * cellSizeCols);
        int border = (int)cellSizeCols/15;
        Size size = new Size(outputSize, outputSize);

        log.debug("cellsize: " + cellSizeRows);
        log.debug("Offset: " + offsetRows);
        log.debug(String.format("src size:\n\trows: %s\n\tcols: %s", src.rows(), src.cols()));

        log.info(String.format("Cutting out %s individual characters...", cellCols*cellRows));
        for(int row = 0; row < cellRows; row++) {
            for(int col = 0; col < cellCols; col++) {
                int x = offsetCols + Math.round(col * cellSizeCols);
                int y = offsetRows + Math.round(row * cellSizeRows);
                //System.out.println(String.format("col: %s \nrow: %s\nx: %s\ny: %s\ncellsize: %s",col, row, x, y, cellSize));
                Mat tmp = src.submat(
                        y + border,
                        Math.round(y + cellSizeRows) - border,
                        x + border,
                        Math.round(x + cellSizeCols) - border
                );
                log.debug(String.format("Resizing characters to %sx%s Pixels", outputSize, outputSize));
                Imgproc.resize(tmp, tmp, size);
                dst.add(tmp);
            }
        }
        return dst;
    }

    public static List<INDArray> convertMatsToIDNArray(List<Mat> mats) {
        List<INDArray> dst = new ArrayList<>();
        for (Mat mat: mats) {
            dst.add(convertMatToINDArray(mat));
        }
        return dst;
    }

    public static INDArray convertMatToINDArray(Mat src) {
        log.debug("Converting opencv Mat to INDArray...");
        NativeImageLoader nim = new NativeImageLoader(src.rows(), src.cols(), src.channels());
        INDArray dst = null;
        try {
            dst = nim.asMatrix(src);
        } catch (IOException e) {
            log.error("Failed to convert opencv Mat to INDArray.");
            System.exit(1);
        }
        return dst;
    }

    public static List<INDArray> loadImage(String file) {
        log.info("Imageloader started.");
        List<INDArray> characters = null;
        try {
            Mat img = preprocessImg(openImg(file));
            List<Point> corners = detectAruCo(img);
            Mat imgAffine = affineTransform(img, corners);
            List<Mat> charactersMat = cutLetters(imgAffine);
            characters = convertMatsToIDNArray(charactersMat);
        } catch (IOException e) {
            log.error("Failed to load image " + file);
            System.exit(1);
        } catch (NoAruComarkersFoundException e) {
            log.error("No AruCo Markers could be detected in the image " + file);
            System.exit(1);
        } catch (TooFewAruComarkersFoundException e) {
            log.error("Too few AruCo markers were detected in the image " + file);
            System.exit(1);
        }
        return characters;
    }

    public static void main(String[] args) {
        Loader.load(opencv_java.class);
        List<INDArray> chars = loadImage("/home/jahlers/schule/msp/images/template_v3_ausgefuellt.png");
    }
}
