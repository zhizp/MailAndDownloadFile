package com.ele.project.common.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ele.project.common.service.LoginService;
import com.ele.project.sysmanager.user.pojo.UserDTO;

@Controller
@RequestMapping("/downFileController")
public class DownFileController {
	
	@Resource
	private LoginService loginService;
	
	/**
     * 从网络Url中下载文件
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
	@RequestMapping("/downLoadFromUrl")
    public void  downLoadFromUrl(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserDTO user=(UserDTO)request.getSession().getAttribute("user");
		String userid=user.getId();
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("id", userid);
		userMap= loginService.getFilePathByid(userMap);
		if(userMap.get("path")==null ||"".equals(userMap.get("path"))) {
			return;
		}
		String filename = userMap.get("path").toString().substring(userMap.get("path").toString().lastIndexOf('/')+1);
        URL url = new URL(userMap.get("path").toString());  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                //设置超时间为3秒
        conn.setConnectTimeout(3*1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();  
        //获取自己数组
        byte[] getData = readInputStream(inputStream);    
		Boolean isIE = false;
        String agent = request.getHeader("USER-AGENT");
        if (null != agent && (-1 != agent.indexOf("MSIE")||-1 != agent.indexOf("Edge"))
                || null != agent && -1 != agent.indexOf("Trident")){
            isIE = true;
        }
		response.setContentType("application/octet-stream");
		response.setStatus(200);
        response.setHeader("Content-Disposition", "attachment;filename="+(isIE?URLEncoder.encode(filename,"UTF-8"):new String(filename.getBytes("utf-8"),"iso-8859-1")));
        response.setCharacterEncoding("utf-8");
        OutputStream os=null;
        try {
			os = response.getOutputStream();
			os.write(getData);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(os!=null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

    }



    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
        byte[] buffer = new byte[1024];  
        int len = 0;  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
        while((len = inputStream.read(buffer)) != -1) {  
            bos.write(buffer, 0, len);  
        }  
        bos.close();  
        return bos.toByteArray();  
    }  

    public static void main(String[] args) {
        try{
            /*downLoadFromUrl("http://101.95.48.97:8005/res/upload/interface/apptutorials/manualstypeico/6f83ce8f-0da5-49b3-bac8-fd5fc67d2725.png",
                    "百度.jpg","E:\\a");
            downLoadFromUrl("http://bos.pgzs.com/rbpiczy/soft/2013/6/21/042fb53d479b499fa20acb2108062e06/thumb_21c278f692d0412784d87ce5eadeebd8_640x1136.jpeg",
            		"thumb_21c278f692d0412784d87ce5eadeebd8_640x1136.jpeg","E:\\a");*/
        }catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    

	
	 
}
