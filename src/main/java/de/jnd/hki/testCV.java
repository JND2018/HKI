package de.jnd.hki;
import org.bytedeco.javacpp.opencv_dnn;
import org.opencv.aruco.Dictionary;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.aruco.Aruco;

import java.util.ArrayList;
//import java.util.Dictionary;
import java.util.List;

public class testCV {
    public static void testAffineTransformation() {
        String file = "/home/jahlers/schule/msp/papier.jpg";
        Mat src = Imgcodecs.imread(file);
        Mat dst = new Mat();

        Point p1 = new Point(90, 43);
        Point p2 = new Point(425, 41);
        Point p3 = new Point(78, 250);
        Point p4 = new Point(0,0);
        Point p5 = new Point(src.cols() - 1, 0);
        Point p6 = new Point(0, src.rows() - 1);
        MatOfPoint2f ma1 = new MatOfPoint2f(p1, p2, p3);
        MatOfPoint2f ma2 = new MatOfPoint2f(p4, p5, p6);

        Mat transformMatrix = Imgproc.getAffineTransform(ma1, ma2);

        System.out.println(src.cols());
        System.out.println(src.rows());
        Size size = new Size(src.cols(), src.rows());
        Imgproc.warpAffine(src, dst, transformMatrix, size);

        Imgcodecs.imwrite("/home/jahlers/schule/msp/papier_affine.jpg", dst);
        System.out.println("Fertig!");
    }

    public static Mat imgImport() {
        Imgcodecs imageCodecs = new Imgcodecs();
        //String file = "/home/jahlers/schule/msp/images/echtespapier.jpg";
        //String file = "/home/jahlers/schule/msp/images/template_v1.png";
        String file = "/home/jahlers/schule/msp/images/template_v3.png";

        Mat src = new Mat();
        src = imageCodecs.imread(file, Imgcodecs.IMREAD_GRAYSCALE);
        Mat dst = new Mat();
        Imgproc.threshold(src, dst, 170, 255, Imgproc.THRESH_BINARY);
        Mat dst2 = new Mat();
        Imgproc.pyrDown(dst,dst2);
        System.out.println("Image loaded.");
        return dst2;
    }

    public static void imgExport(Mat src, String filename) {
        Imgcodecs imageCodecs = new Imgcodecs();
        imageCodecs.imwrite("/home/jahlers/schule/msp/images/" + filename, src);
        System.out.println("Image saved.");
    }

    public static void detectEdges(Mat mat) {
        List<MatOfPoint> contours = new ArrayList();
        Mat edges = new Mat();
        Mat dest = Mat.zeros(mat.size(), CvType.CV_8UC3);
        Scalar black = new Scalar(0, 0, 0);
        Scalar white = new Scalar(255, 255, 255);
        //Imgproc.findContours(mat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_KCOS);
        //Imgproc.drawContours(dest, contours, -1, white);
        Imgproc.Canny(mat, edges, 60., 60.*3, 5, false);
        imgExport(edges, "edges.png");
    }

    public static Mat detectCross(Mat src) {
        MatOfPoint corners = new MatOfPoint();
        Mat dest = new Mat();
        Imgproc.pyrDown(src, dest);
        Imgproc.goodFeaturesToTrack(dest, corners, 50, 0.70, 100.);
        for (Point corner: corners.toArray()) {
            corner.x = corner.x*2;
            corner.y = corner.y*2;
            Imgproc.circle(src, corner, 6, new Scalar(0, 0, 255), 2);
        }
        return src;
    }

    public static Mat detectCrossCascade(Mat mat) {
        String xmlFile = "/home/jahlers/schule/msp/images/training/cascade/cascade.xml";
        CascadeClassifier classifier = new CascadeClassifier(xmlFile);
        MatOfRect crossDetections = new MatOfRect();
        classifier.detectMultiScale(mat, crossDetections);
        System.out.println(xmlFile);
        System.out.println(String.format("Detected %s crosses", crossDetections.toArray().length));

        for (Rect rect : crossDetections.toArray()) {
            Imgproc.rectangle(
                mat,                                                        // where to draw the box
                new Point(rect.x, rect.y),                                  // bottom left
                new Point(rect.x + rect.width, rect.y + rect.height), // top right
                new Scalar(0, 0, 255),
                3                                                  // RGB colour
            );
        }

        return mat;
    }

    public static List<Point> detectAruCo(Mat src) {
        List<Mat> markers = new ArrayList<>();
        Mat ids = new Mat();
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
        Aruco.detectMarkers(src, dictionary, markers, ids);
        List<Point> centers = new ArrayList<>();
        for (Mat marker: markers) {
            /*System.out.println(marker.get(0,0)[0]);
            Point cornerTopLeft =  new Point(marker.get(0,0));
            Point cornerBottomRight = new Point(marker.get(0,2));
            Imgproc.rectangle(
                    src,
                    cornerTopLeft,
                    cornerBottomRight,
                    new Scalar(0,0,255),
                    3
            );*/
            Point a = new Point(marker.get(0,0));
            Point b = new Point(marker.get(0,1));
            Point c = new Point(marker.get(0,2));
            Point d = new Point(marker.get(0,3));
            double x = 0.25 * (a.x + b.x + c.x +d.x);
            double y = 0.25 * (a.y + b.y + c.y +d.y);
            centers.add(new Point(x,y));
        }
        return centers;
    }
}
