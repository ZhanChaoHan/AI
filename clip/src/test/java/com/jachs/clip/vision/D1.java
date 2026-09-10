package com.jachs.clip.vision;

import java.nio.file.Paths;

import org.junit.Test;

import ai.djl.modality.cv.Image;
import cn.smartjavaai.clip.config.ClipModelConfig;
import cn.smartjavaai.clip.enums.ClipModelEnum;
import cn.smartjavaai.clip.model.ClipModel;
import cn.smartjavaai.clip.model.ClipModelFactory;
import cn.smartjavaai.common.cv.SmartImageFactory;
import cn.smartjavaai.common.entity.R;
import cn.smartjavaai.common.enums.DeviceEnum;

/***
 * @author zhanchaohan
 */
public class D1 {
	
	public void extractImageFeatures() throws Exception {
	    try (ClipModel model = getModel()) {
	       Image image = SmartImageFactory.getInstance()
	 .fromFile(Paths.get("src/main/resources/clip/A.png"));
	        R<float[]> features = model.extractImageFeatures(image);
	        if (features.isSuccess()) {
	            System.out.println("图片特征维度: {}"+features.getData().length); // 512
	 } else {
	            System.out.println("失败:"+features.getMessage());
	        }
	    }
	}

	private ClipModel getModel() {
		ClipModelConfig config = new ClipModelConfig();
		config.setModelEnum(ClipModelEnum.OPENAI);

		// 必须显式指向本地权重文件(从官方百度网盘下载)
		config.setModelPath("/path/to/clip-vit-base-patch32.pt");

		// 也可以从 jar 包内加载,适合打包发布
		// config.setModelPath("jar://META-INF/models/clip/openai.zip");

		// CPU/GPU 切换
		config.setDevice(DeviceEnum.CPU); // 默认// config.setDevice(DeviceEnum.GPU);
		// config.setGpuId(0);

		// 获取模型实例(单例)
		ClipModel model = ClipModelFactory.getInstance().getModel(config);
		return model;
	}

	@Test
	public void t1() throws Exception {
		extractImageFeatures();
	}
}
