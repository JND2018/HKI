package de.jnd.hki.controller;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class BaseUtils {
	private static Logger log = LoggerFactory.getLogger(InputController.class);

	public static Map<String,String> convertArgsToMap(String ... args ){
		Map<String,String> map = new HashMap<>();
		Arrays.stream(args).forEach((String x) ->{
			if(x.contains("=")){
				map.put(x.split("=")[0],x.split("=")[1]);
			}
		});
		return map;
	}

	public static File fileChose(Window window) {
		FileChooser fc = new FileChooser();
		return fc.showOpenDialog(null);
	}

	public static String getTargetLocation(){
		URL location = BaseUtils.class.getProtectionDomain().getCodeSource().getLocation();
		return location.getFile().replace("classes/","");
	}

	public static boolean isDebug(){
		return java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
	}

	public static boolean isEclipse() {
		boolean isEclipse = System.getProperty("java.class.path").toLowerCase().contains("eclipse");
		return isEclipse;
	}

	public static boolean isIntelliJ() {
		boolean isIntelliJ = System.getProperty("java.class.path").toLowerCase().contains("idea");
		return isIntelliJ;
	}

	public static List<INDArray> convertMatsToINDArray(List<Mat> mats) throws IOException {
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

	public static Mat convertINDArrayToMat(INDArray src) {
		log.debug("Converting INDArray to opencv Mat...");
		return new Mat(src.rows(), src.length()/src.rows(), 0, src.data().asNio());
	}

}
