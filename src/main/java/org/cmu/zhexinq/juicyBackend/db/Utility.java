package org.cmu.zhexinq.juicyBackend.db;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.codec.binary.Base64;

/**
 * provide helper functions for computations
 * Created by qiuzhexin on 11/27/15.
 */
public class Utility {
	
    // compute the distance of a pair of (lat, lon) (in meters)
    public static double computeDistanceUsingGeoLoc(double lat1, double lon1,
                                             double lat2, double lon2) {
        double R = 6371000;
        double rad_phi1 = lat1 * 3.14 / 180;
        double rad_phi2 = lat2 * 3.14 / 180;
        double delta_phi = (lat2 - lat1) * 3.14 / 180;
        double delta_lambda = (lon2 - lon1) * 3.14 / 180;
        double a = Math.sin(delta_phi/2) * Math.sin(delta_phi/2) +
                Math.cos(rad_phi1) * Math.cos(rad_phi2) *
                        Math.sin(delta_lambda/2) * Math.sin(delta_lambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    
    // encode a disk image to string
    public static String convertImgToStr(String pathToImg) {
    	String encodedImg = null;
    	try {
	    	BufferedImage image = ImageIO.read(new File(pathToImg));
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	ImageIO.write(image, "jpg", baos);
	    	baos.flush();
	    	byte[] imageInByte = baos.toByteArray();
	    	baos.close();
	        encodedImg = Base64.encodeBase64String(imageInByte);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return encodedImg;
    }
    
    // decode a string to image and wrtie to disk
    public static void convertStrToImg(String pathToStore, String imgStr, String imgFormat) {
    	try {
    		byte[] imageInByte = Base64.decodeBase64(imgStr);
    		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageInByte));
    		ImageIO.write(image, imgFormat, new File(pathToStore));
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    // write a image to disk, and return the path
    public static String writeImageToWebContent(String imgStr, ServletContext context, long imgId, String imgFormat) {
    	String contentPath = context.getRealPath("/");
//    	String contentPath = "/Users/qiuzhexin/Documents/workspace/juicyBackend/imgShareFolder/";
    	contentPath = contentPath + imgId + "." + imgFormat;
    	System.out.println("The image path: " + contentPath);
    	convertStrToImg(contentPath, imgStr, imgFormat);
    	String ip = null;
    	try {
    		ip = InetAddress.getLocalHost().getHostAddress();
    		System.out.println("get IP address: " + ip);
    	} catch (UnknownHostException e) {
    		e.printStackTrace();
    	}
    	return imgId + "." + imgFormat;
    }
    
}
