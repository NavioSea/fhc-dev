package org.fh.util;

import java.util.*;

import net.sf.json.JSONObject;

/**
 * 说明：人脸识别
 * 作者：FH Admin Q313596790
 * 官网：www.fhadmin.org
 */
public class FaceMatch {

    public static String faceMatch(List<Object> list,String accessToken) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {
            String param = GsonUtils.toJson(list);
            String result = HttpUtil.post(url, accessToken, "application/json", param);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean getScore(String PHOTODATA1,String PHOTODATA2,String accessToken) {
    	
    	/*人脸的类型  face_type
    	LIVE：表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等， 
    	IDCARD：表示身份证芯片照：二代身份证内置芯片中的人像照片， 
    	WATERMARK：表示带水印证件照：一般为带水印的小图，如公安网小图 
    	CERT：表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 
    	INFRARED 表示红外照片：使用红外相机拍摄的照片
    	默认LIVE*/
    	
    	/*图片质量控制  quality_control
    	NONE: 不进行控制 
    	LOW:较低的质量要求 
    	NORMAL: 一般的质量要求 
    	HIGH: 较高的质量要求 
    	默认 NONE 
    	若图片质量不满足要求，则返回结果中会提示质量检测失败*/
    	
    	/*活体检测控制   liveness_control
    	NONE: 不进行控制 
    	LOW:较低的活体要求(高通过率 低攻击拒绝率) 
    	NORMAL: 一般的活体要求(平衡的攻击拒绝率, 通过率) 
    	HIGH: 较高的活体要求(高攻击拒绝率 低通过率) 
    	默认 NONE 
    	若活体检测结果不满足要求，则返回结果中会提示活体检测失败*/
    	
    	List<Object> list = new ArrayList<Object>();
    	Map<String,String> map1 = new HashMap<String,String>();
    	map1.put("image", PHOTODATA1);
    	map1.put("image_type", "BASE64");
    	map1.put("face_type", "LIVE");
    	map1.put("quality_control", "NONE");
    	map1.put("liveness_control", "NONE");
    	list.add(map1);
    	Map<String,String> map2 = new HashMap<String,String>();
    	map2.put("image", PHOTODATA2);
    	map2.put("image_type", "BASE64");
    	map2.put("face_type", "LIVE");
    	map2.put("quality_control", "NONE");
    	map2.put("liveness_control", "NONE");
    	list.add(map2);
    	String resultStr = FaceMatch.faceMatch(list, accessToken);
        JSONObject jsonMsg = JSONObject.fromObject(resultStr);
		String error_msg = jsonMsg.getString("error_msg");
		String score = "0";
		if("SUCCESS".equals(error_msg)) {
			String result = jsonMsg.getString("result");
			JSONObject jsonResult = JSONObject.fromObject(result);
			score = jsonResult.getString("score");
		}
		return Float.parseFloat(score) > 80; //score 人脸相似度得分，推荐阈值80分
    }
    
}
