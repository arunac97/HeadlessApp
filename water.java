//$Id$
package main;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CountDownLatch;



import org.json.JSONObject;

public class Water {
	public static int jsid;
	public static int printid;
	public static int pageEnableId;
	public static int setEmulatedMediaId;
	public static int pageNavigateId;
	public static int setMobileScreenId;
	public static int screenshotId;


    public static int layoutMetricsId;
    public static int setFullViewportId;
    
    public static int runtimeEnableId;
    public static int logEnableId;
    
    	//desktop resolution
//    public static int contentWidth = 1920;
//    public static int contentHeight = 1080;
    
    	//tablet resolution
    
    
    public static int contentWidth = 360;
    public static int contentHeight = 640;

	public static void main(String args[]) {
		try {
			CountDownLatch latch = new CountDownLatch(1);
			String webSocketDebuggerUrl = "ws://localhost:9222/devtools/page/B6300B172DFB9B8A5CAE82A1506927A8";
			HttpClient client = HttpClient.newHttpClient();
			ChromeWebSocketListener ChromeWebSocketListenerInstance = new ChromeWebSocketListener(latch);
		    WebSocket webSocket = client.newWebSocketBuilder()
		            .buildAsync(URI.create(webSocketDebuggerUrl), ChromeWebSocketListenerInstance)
		            .join();
		    
		    pageEnableId = sendMessage(webSocket, "Page.enable", new JSONObject());  // Enable page events
		    System.out.println("Page enable --started : "+ pageEnableId);
		    latch.await();
		    
		    latch = new CountDownLatch(1);
			ChromeWebSocketListenerInstance.setLatch(latch);
		    runtimeEnableId = sendMessage(webSocket, "Runtime.enable", new JSONObject());
		    System.out.println("Runtime.enable --started : " + runtimeEnableId);
		    latch.await();

		    latch = new CountDownLatch(1);
			ChromeWebSocketListenerInstance.setLatch(latch);
		    logEnableId = sendMessage(webSocket, "Log.enable", new JSONObject());
		    System.out.println("Log.enable --started : " + logEnableId);
		    latch.await();
		    
		    latch = new CountDownLatch(1);
			ChromeWebSocketListenerInstance.setLatch(latch);
		    setMobileScreenId = setMobileScreen(webSocket);
		    System.out.println("setMobileScreen --started : "+ setMobileScreenId);
		    latch.await();
		    
		    latch = new CountDownLatch(1);
			ChromeWebSocketListenerInstance.setLatch(latch);
		    setEmulatedMediaId = sendMessage(webSocket, "Emulation.setEmulatedMedia", new JSONObject().put("media", "screen"));
		    System.out.println("Emulation setEmulatedMedia --started : "+setEmulatedMediaId);
		    latch.await();
		    
		    
		    latch = new CountDownLatch(1);
		    ChromeWebSocketListenerInstance.setLatch(latch);
		    pageNavigateId = sendMessage(webSocket, "Page.navigate", new JSONObject().put("url", "https://arunac97.github.io/samplesite.com/newcanvas.html"));

//		    pageNavigateId = sendMessage(webSocket, "Page.navigate", new JSONObject().put("url", "https://arunac97.github.io/samplesite.com/newcanvas.html"));
		    
		    System.out.println("Page navigate --started : "+pageNavigateId);
		    latch.await();
		    
		    Thread.sleep(3000);
		    
		    latch = new CountDownLatch(1);
		    ChromeWebSocketListenerInstance.setLatch(latch);
		    String jsText = getValidJs();
		    jsid = executeJsAfterPageLoad(webSocket, jsText);
		    System.out.println("Runtime js evaluate --started : "+jsid);
		    latch.await();

            Thread.sleep(3000);
            
		    // 1. Get layout metrics (content size)
            latch = new CountDownLatch(1);
            ChromeWebSocketListenerInstance.setLatch(latch);
            layoutMetricsId = getLayoutMetrics(webSocket);
            System.out.println("getLayoutMetrics --started : "+layoutMetricsId);
            latch.await();
		    
		    latch = new CountDownLatch(1);
		    ChromeWebSocketListenerInstance.setLatch(latch);
		    printid = takePdf(webSocket);
		    System.out.println("Page printToPDF --started : "+printid);
		    latch.await();

		    Thread.sleep(3000);
			// --- FULL PAGE SCREENSHOT LOGIC ---
            
            
//            // 2. Set viewport to content size
//            latch = new CountDownLatch(1);
//            ChromeWebSocketListenerInstance.setLatch(latch);
//            setFullViewportId = setFullViewport(webSocket, contentWidth, contentHeight);
//            latch.await();
//            
//
            // 3. Capture screenshot
//            latch = new CountDownLatch(1);
//            ChromeWebSocketListenerInstance.setLatch(latch);
//            screenshotId = takeScreenshot(webSocket);
//            System.out.println("Page.captureScreenshot called : " + screenshotId);
//            latch.await();
//            System.out.println("Screenshot finished");
//
//		    System.out.println("Everything executed successfully");
		    
		} catch(Exception e) {
			System.out.println("Exception");
			System.out.println(e.toString());
		}
	}

	private static int executeJsAfterPageLoad(WebSocket webSocket, String jsText) {
		return sendMessage(webSocket, "Runtime.evaluate", new JSONObject().put("expression", jsText));
	}

	private static int takePdf(WebSocket webSocket) {
		System.out.println("takePdf -  contentWidth : "+contentWidth);
		System.out.println("takePdf -  contentHeight : "+contentHeight);
		JSONObject params = new JSONObject();
		params.put("paperWidth", contentWidth/96);
		params.put("paperHeight", contentHeight/96);

//		params.put("paperWidth", 364/96);
//		params.put("paperHeight", 640/96);
		params.put("marginTop", 0);
		params.put("marginBottom", 0);
		params.put("marginLeft", 0);
		params.put("marginRight", 0);
		params.put("scale", 1);
		params.put("printBackground", true);
		return sendMessage(webSocket, "Page.printToPDF", params);
	}
	private static int setMobileScreen(WebSocket webSocket) {
		
//		Mobile: width - 360pixel, height - 640pixel 
//		Tablet:width - 768pixel, height - 1024pixel
		System.out.println("contentWidth & contentHeight before setting mobilescreen " + contentWidth + " X " + contentHeight );
		JSONObject params = new JSONObject();
		params.put("width", contentWidth);
		params.put("height", contentHeight);
		params.put("deviceScaleFactor", 1);
		params.put("mobile", false);
	    return sendMessage(webSocket, "Emulation.setDeviceMetricsOverride", params);  // Enable page events
	}
    private static int sendMessage(WebSocket webSocket, String method, JSONObject params) {
        JSONObject message = new JSONObject();
        int id = (int) (Math.random() * 10000);
        message.put("id", id);
        message.put("method", method);
        message.put("params", params);

//        System.out.println("Sending message: " + message);
        webSocket.sendText(message.toString(), true);
        return id;
    }
    private static String getValidJs() {
    	String result = "";
    	try {
	    	String jspath = "/Users/space/Desktop/AA_HeadlessReports/heatmaplitejswithdata.js";
	    	FileInputStream fis = new FileInputStream(jspath);
	        InputStreamReader isr = new InputStreamReader(fis);
	        BufferedReader br = new BufferedReader(isr);
	        StringBuilder content = new StringBuilder();
	        while ((result = br.readLine()) != null) {
				    content.append(result);
			}
	        result = content.toString();
				br.close();
			}catch (IOException e) {
				e.printStackTrace();
			} 
    	
    	return result;
    }

	 // 1. Get layout metrics
    private static int getLayoutMetrics(WebSocket webSocket) {
        return sendMessage(webSocket, "Page.getLayoutMetrics", new JSONObject());
    }

    // 2. Set viewport to content size
    private static int setFullViewport(WebSocket webSocket, int width, int height) {
        JSONObject params = new JSONObject();
        params.put("width", width);
        params.put("height", height);
        params.put("deviceScaleFactor", 1);
        params.put("mobile", true);
        return sendMessage(webSocket, "Emulation.setDeviceMetricsOverride", params);
    }

    // 3. Take screenshot (full page)
    private static int takeScreenshot(WebSocket webSocket) {
        JSONObject params = new JSONObject();
        params.put("format", "png");
        params.put("captureBeyondViewport", true);
        params.put("fromSurface", true);
        return sendMessage(webSocket, "Page.captureScreenshot", params);
    }
}
