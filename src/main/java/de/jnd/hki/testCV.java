package de.jnd.hki;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class testCV {
    public static void test() {
        String file = "/home/jahlers/schule/mittelstufenprojekt/papier.jpg";
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

        Imgcodecs.imwrite("/home/jahlers/schule/mittelstufenprojekt/papier_affine.jpg", dst);
        System.out.println("Fertig!");
    }
}
