package de.jnd.hki.controller;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_java;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.imgproc.Moments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class NoAruCoMarkersFoundException extends Exception {
     NoAruCoMarkersFoundException() {
        super();
    }
}

class TooFewAruCoMarkersFoundException extends Exception {
    TooFewAruCoMarkersFoundException() {
        super();
    }
}

public class InputController {
    private static Logger log = LoggerFactory.getLogger(InputController.class);
    protected static boolean DEBUG = false;
    private static String DEBUGPATH = BaseUtils.getTargetLocation() + "/debugimages";
    private static final int DEFAULTCELLCOLS = 14;
    private static final int DEFAULTCELLROWS = 22;
    private static final float DEFAULTCELLOFFSET = 1.5f;
    private static final Size DEFAULTCHARSIZE = new Size(20, 20);
    private static final Size DEFAULTOUTPUTSIZE = new Size(28, 28);

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
        Imgproc.threshold(src, dst, 145, 255, Imgproc.THRESH_BINARY);
        if (DEBUG) Imgcodecs.imwrite(DEBUGPATH + "/preprocess.png", dst);
        log.info("Preprocessing image...");
        return dst;
    }

    public static List<Point> detectAruCo(Mat src) throws NoAruCoMarkersFoundException {
        List<Mat> markers = new ArrayList<>();
        Mat ids = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
        Aruco.detectMarkers(src, dictionary, markers, ids);
        Point[] centers = new Point[4];
        for (int i = 0; i < markers.size(); i++) {
            Point a = new Point(markers.get(i).get(0,0));
            Point b = new Point(markers.get(i).get(0,1));
            Point c = new Point(markers.get(i).get(0,2));
            Point d = new Point(markers.get(i).get(0,3));
            double x = 0.25 * (a.x + b.x + c.x + d.x);
            double y = 0.25 * (a.y + b.y + c.y + d.y);
            int id = (int) ids.get(i, 0)[0];
            centers[id] = new Point(x,y);
        }
        List<Point> centersList = Arrays.asList(centers);
        if (centersList.size() <= 0) {
            throw new NoAruCoMarkersFoundException();
        }
        log.info(String.format("Detected %s AruCo Markers", centersList.size()));
        return centersList;
    }

    public static Mat perspectiveTransform(Mat src, List<Point> corners) throws TooFewAruCoMarkersFoundException{
        if (corners.size() < 4) {
            throw new TooFewAruCoMarkersFoundException();
        }

        MatOfPoint2f srcMat = new MatOfPoint2f(
                corners.get(0),
                corners.get(1),
                corners.get(2),
                corners.get(3)
        );
        MatOfPoint2f dstMat = new MatOfPoint2f(
                new Point(0,0),
                new Point(src.cols()-1,0),
                new Point(0, src.rows()-1),
                new Point(src.cols()-1, src.rows()-1)
        );
        Mat warpMat = Imgproc.getPerspectiveTransform(srcMat, dstMat);
        Mat dst = new Mat();
        log.info("Warping image...");
        Imgproc.warpPerspective(src, dst, warpMat, new Size(src.cols(), src.rows()));
        log.info("Inverting image...");
        Imgproc.threshold(dst, dst, 0, 255, Imgproc.THRESH_BINARY_INV);
        return dst;
    }

    public static List<Mat> cutLetters(Mat src, int cellCols, int cellRows, float cellOffset, Size charSize, Size imgSize) {
        List<Mat> dst = new ArrayList<>();

        float cellSizeRows = (float)src.rows() / (cellRows + (cellOffset * 2));
        float cellSizeCols = (float)src.cols() / (cellCols + (cellOffset * 2));
        int offsetRows = Math.round(cellOffset * cellSizeRows);
        int offsetCols = Math.round(cellOffset * cellSizeCols);
        int border = (int)cellSizeCols/12;

        if (DEBUG) Imgcodecs.imwrite(DEBUGPATH + "/cutletters.png", src);

        log.debug("cellsize: " + cellSizeRows);
        log.debug("Offset: " + offsetRows);
        log.debug(String.format("src size:\n\trows: %s\n\tcols: %s", src.rows(), src.cols()));

        log.info(String.format("Cutting out %s individual characters...", cellCols*cellRows));
        for(int row = 0; row < cellRows; row++) {
            for(int col = 0; col < cellCols; col++) {
                int x = offsetCols + Math.round(col * cellSizeCols);
                int y = offsetRows + Math.round(row * cellSizeRows);
                Mat tmp = src.submat(
                        y + border,
                        Math.round(y + cellSizeRows) - border,
                        x + border,
                        Math.round(x + cellSizeCols) - border
                );
                log.debug(String.format("Resizing characters to %sx%s Pixels", charSize.width, charSize.height));
                Imgproc.resize(tmp, tmp, charSize);
                Mat tmp2 = new Mat(imgSize, tmp.type());
                Moments moments = Imgproc.moments(tmp);

                Point centerOfMass = new Point(
                        moments.m10 / moments.m00,
                        moments.m01 / moments.m00
                );
                Point centerGeom = new Point(tmp.cols(), tmp.rows());
                Point centerDelta = new Point(
                        Math.round(centerOfMass.x - centerGeom.x + 1),
                        Math.round(centerOfMass.y - centerGeom.y + 1)
                );
                for (int u = 0; u < tmp.cols(); u++) {
                    for (int v = 0; v < tmp.rows(); v++) {
                        tmp2.put(
                                (int)(v + centerDelta.y),
                                (int)(u + centerDelta.x),
                                tmp.get(v, u)
                        );
                    }
                }

                if (BaseUtils.isDebug()) Imgcodecs.imwrite(String.format(DEBUGPATH + "/chars/%s%s.png", row, col), tmp2);
                dst.add(tmp2);
            }
        }
        return dst;
    }

    public static List<INDArray> convertMatsToIDNArray(List<Mat> mats) throws IOException {
        List<INDArray> dst = new ArrayList<>();
        for (Mat mat: mats) {
            dst.add(convertMatToINDArray(mat));
        }
        return dst;
    }

    public static INDArray convertMatToINDArray(Mat src) throws IOException {
        log.debug("Converting opencv Mat to INDArray...");
        NativeImageLoader loader = new NativeImageLoader(src.rows(), src.cols(), src.channels());
        opencv_core.Mat src2 = new opencv_core.Mat((Pointer)null) { { address = src.getNativeObjAddr(); } };
        INDArray dst = loader.asMatrix(src2);

        if (dst == null) {
            throw new IOException("Failed to convert opencv Mat to INDArray.");
        }
        return dst;
    }

    public static List<INDArray> loadImage(String file) throws InputException {
        if (DEBUG) {
            new File(DEBUGPATH + "/chars").mkdirs();
        }

        Loader.load(opencv_java.class); // load native openCV functions
        log.info("Imageloader started.");
        List<Mat> charactersMat = null;
        List<INDArray> characters;
        try {
            Mat img = preprocessImg(openImg(file));
            List<Point> corners = detectAruCo(img);
            Mat imgTransformed = perspectiveTransform(img, corners);
            charactersMat = cutLetters(imgTransformed, DEFAULTCELLCOLS, DEFAULTCELLROWS, DEFAULTCELLOFFSET, DEFAULTCHARSIZE, DEFAULTOUTPUTSIZE);
            characters = convertMatsToIDNArray(charactersMat);
        } catch (IOException e) {
            if (charactersMat == null) {
                log.error("Failed to load image " + file);
            } else {
                log.error("Failed to convert opencv Mat to INDArray.");
            }
            throw new InputException();
        } catch (NoAruCoMarkersFoundException e) {
            log.error("No AruCo Markers could be detected in the image " + file);
            throw new InputException();
        } catch (TooFewAruCoMarkersFoundException e) {
            log.error("Too few AruCo markers were detected in the image " + file);
            throw new InputException();
        }
        log.info("Characters were imported successfully.");
        return characters;
    }
}