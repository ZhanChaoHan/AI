package com.jachs.clip.inference4j;

import org.junit.Test;

import io.github.inference4j.multimodal.ClipClassifier;
import io.github.inference4j.vision.Classification;
import java.nio.file.Path;
import java.util.List;
/***
 * @author zhanchaohan
 */
public class D1 {

	@Test
	public void t1() {
		try (var clip = ClipClassifier.builder().build()) {
		    List<Classification> results = clip.classify(
		        Path.of("d://A.png"),
		        List.of("cat", "dog", "bird", "car", "a plate of food"));
		    
		    for (Classification cf : results) {
				System.out.println(cf.toString());
			}
		}
	}
}
