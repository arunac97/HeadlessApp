//$Id$
package main;

import java.io.FileOutputStream;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

/* pdf box */
 import java.io.ByteArrayInputStream;
import java.io.InputStream;


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPage;
 

import org.json.JSONArray;
import org.json.JSONObject;

public class ChromeWebSocketListener implements Listener {

    private CountDownLatch latch;
    private StringBuffer sb;

    public ChromeWebSocketListener(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("✅ Connected to Chrome WebSocket.");
        webSocket.request(Long.MAX_VALUE);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        try {
            if (sb == null) sb = new StringBuffer();
            sb.append(data);

            if (!last) {
                webSocket.request(1);
                return CompletableFuture.completedFuture(null);
            }

            String message = sb.toString();
            sb = null;

            JSONObject result = new JSONObject(message);
            processMessage(result, webSocket);
        } catch (Exception e) {
            System.err.println("❌ Exception in onText: " + e.getMessage());
            e.printStackTrace();
        }

        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        System.out.println("⚠️ Received unexpected binary data!");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("ℹ️ WebSocket closed: " + reason);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.err.println("🚨 WebSocket error: " + error.getMessage());
        if (latch != null) latch.countDown(); // Prevent deadlock
    }

    private void processMessage(JSONObject result, WebSocket webSocket) {
        if (result.has("method") && result.getString("method").equals("Page.loadEventFired")) {
            System.out.println("📄 Page loaded");
            latch.countDown();
            return;
        }
        if (result.has("method") && result.getString("method").equals("Runtime.consoleAPICalled")) {
        	handleConsoleLog(result);
            latch.countDown();
            return;
        }
        if (result.has("method") && result.getString("method").equals("Log.entryAdded")) {
        	handleLogEntry(result);
            latch.countDown();
            return;
        }

        if (result.has("id")) {
            int id = result.getInt("id");

            if (id == Water.pageEnableId) {
                System.out.println("✅ Page.enable completed: " + id);
            } else if (id == Water.setMobileScreenId) {
                System.out.println("✅ setMobileScreen completed: " + id);
            } else if (id == Water.setEmulatedMediaId) {
                System.out.println("✅ Emulation.setEmulatedMedia completed: " + id);
            } else if (id == Water.pageNavigateId) {
                System.out.println("✅ Page.navigate completed: " + id);
            } else if (id == Water.jsid) {
                System.out.println("✅ JS evaluation completed: " + id);
            } else if (id == Water.printid) {
                handlePdfResult(result);
                return; // Don't double-count latch below
            } else if (id == Water.screenshotId) {
                handleScreenshotResult(result);
                return;
            } else if (id == Water.layoutMetricsId) {
            	System.out.println("✅ getLayoutMetrics --completed: " + id);
                JSONObject metrics = result.getJSONObject("result").getJSONObject("cssContentSize");
                Water.contentWidth = metrics.getInt("width");
                Water.contentHeight = metrics.getInt("height");
                System.out.println("📏 Content size: " + Water.contentWidth + "x" + Water.contentHeight);
            } else if (id == Water.setFullViewportId) {
                System.out.println("✅ Full viewport metrics override completed: " + id);
            }

            latch.countDown(); // For all other handled responses
        }
    }

    private void handlePdfResult(JSONObject result) {
        try {
            String base64Data = result.getJSONObject("result").getString("data");
            byte[] pdfBytes = Base64.getDecoder().decode(base64Data);
            try {
            	InputStream inputStream = new ByteArrayInputStream(pdfBytes);
                    PDDocument document = Loader.loadPDF(inputStream);

                   int pageIndex = 0;
                   for (PDPage page : document.getPages()) {
                       PDRectangle mediaBox = page.getMediaBox();
                       float width = mediaBox.getWidth();
                       float height = mediaBox.getHeight();

                       System.out.printf("Page %d -> Width: %.2fpt, Height: %.2fpt%n", ++pageIndex, width, height);
                   }
            }
            catch (Throwable t) {
                System.err.println("Caught Throwable:");
                t.printStackTrace(); // prints full stack trace
            }
            
            String filePath = getTimestampedFilePath("pdf");

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
                System.out.println("📝 PDF saved: " + filePath);
                System.out.println("✅ Page.printToPDF completed: " + result.get("id"));
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving PDF: " + e.getMessage());
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private void handleScreenshotResult(JSONObject result) {
        try {
            String base64Data = result.getJSONObject("result").getString("data");
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            String filePath = getTimestampedFilePath("png");

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
                System.out.println("🖼️ Screenshot saved: " + filePath);
                System.out.println("✅ Page.captureScreenshot completed: " + result.get("id"));
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving screenshot: " + e.getMessage());
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    private String getTimestampedFilePath(String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM_dd_hh_mm_ss_a");
        String timestamp = LocalDateTime.now().format(formatter);
        return "/Users/space/Desktop/AA_HeadlessReports/file_" + timestamp + "." + extension;
    }
    
    private void handleConsoleLog(JSONObject result) {
        JSONObject params = result.getJSONObject("params");
        String type = params.getString("type");
        System.out.print("🟡 Console[" + type + "]: ");
        JSONArray args = params.getJSONArray("args");
        for (int i = 0; i < args.length(); i++) {
            JSONObject argObj = args.getJSONObject(i);
            if (argObj.has("value")) {
                System.out.print(argObj.get("value") + " ");
            } else {
                System.out.print(argObj.toString() + " ");
            }
        }
        System.out.println();
    }

    private void handleLogEntry(JSONObject result) {
        JSONObject entry = result.getJSONObject("params").getJSONObject("entry");
        String source = entry.optString("source", "unknown");
        String level = entry.optString("level", "info");
        String text = entry.optString("text", "");
        System.out.println("📋 Log[" + source + "][" + level + "]: " + text);
    }
}
