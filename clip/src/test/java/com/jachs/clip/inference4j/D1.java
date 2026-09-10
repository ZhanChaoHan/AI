package com.jachs.clip.inference4j;


import org.junit.Test;

import cn.smartjavaai.common.utils.ImageUtils;
import io.github.inference4j.multimodal.ClipClassifier;
import io.github.inference4j.vision.Classification;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
/***
 * @author zhanchaohan
 */
public class D1 {

	@Test
	public void t1() {
		try {
			BufferedImage image=ImageIO.read(new File("e:\\A.png"));
			List<String> candidateLabels=new ArrayList<String>();
			candidateLabels.add("什么动物");
			candidateLabels.add("图片中是什么");
			ClipClassifier clip = ClipClassifier.builder().build();
		    List<Classification> results = clip.classify(image,candidateLabels);
		    
		    for (Classification cf : results) {
				System.out.println(cf.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
