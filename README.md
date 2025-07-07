
# 💻 Headless Chrome PDF Generator (Java + WebSocket)

This project demonstrates how to automate a headless Google Chrome instance using **Java + WebSocket** to:

- Load a web page
- Inject a custom JavaScript file (`bundle.js`)
- Export the final rendered page as a **PDF**

---

## 🧰 Requirements

- Java 8 or later
- Google Chrome installed
- A terminal or command line interface
- `bundle.js` (JavaScript code to modify the page)

---

## 🚀 Quick Start

### 1. Launch Headless Chrome with Remote Debugging

To use the Chrome DevTools Protocol, start Chrome with debugging enabled.

#### For **macOS/Linux**:

```bash
chrome --remote-debugging-port=9222   --remote-debugging-address=127.0.0.1   --headless   --use-gl=angle   --use-angle=swiftshader-webgl   --hide-scrollbars   --disable-translate   --disable-extensions   --disable-background-networking   --mute-audio   --no-first-run   --allow-http-screen-capture   --allow-running-insecure-content   --unsafely-treat-insecure-origin-as-secure   --allow-external-pages   --disable-offline-auto-reload-visible-only   --disable-overscroll-edge-effect   --no-default-browser-check   --disable-plugin-power-saver   --disable-background-timer-throttling   --disable-client-side-phishing-detection   --disable-default-apps   --disable-hang-monitor   --disable-popup-blocking   --disable-prompt-on-repost   --disable-sync   --metrics-recording-only   --safebrowsing-disable-auto-update   --ignore-certificate-errors   --disable-machine-cert-request   --ignore-certificate-errors-spki-list   --ignore-urlfetcher-cert-requests   --remote-allow-origins=http://127.0.0.1:9222
```

#### For **Windows PowerShell**:

```powershell
chrome --remote-debugging-port=9222 ^
  --remote-debugging-address=127.0.0.1 ^
  --headless ^
  --use-gl=angle ^
  --use-angle=swiftshader-webgl ^
  --hide-scrollbars ^
  --disable-translate ^
  --disable-extensions ^
  --disable-background-networking ^
  --mute-audio ^
  --no-first-run ^
  --allow-http-screen-capture ^
  --allow-running-insecure-content ^
  --unsafely-treat-insecure-origin-as-secure ^
  --allow-external-pages ^
  --disable-offline-auto-reload-visible-only ^
  --disable-overscroll-edge-effect ^
  --no-default-browser-check ^
  --disable-plugin-power-saver ^
  --disable-background-timer-throttling ^
  --disable-client-side-phishing-detection ^
  --disable-default-apps ^
  --disable-hang-monitor ^
  --disable-popup-blocking ^
  --disable-prompt-on-repost ^
  --disable-sync ^
  --metrics-recording-only ^
  --safebrowsing-disable-auto-update ^
  --ignore-certificate-errors ^
  --disable-machine-cert-request ^
  --ignore-certificate-errors-spki-list ^
  --ignore-urlfetcher-cert-requests ^
  --remote-allow-origins=http://127.0.0.1:9222
```

---

## 📁 Project Structure

```
your-project/
├── bundle.js                        # Custom JavaScript to inject
└── src/
    ├── Water.java                   # Main logic to control Chrome and generate PDF
    └── ChromeWebSocketListener.java# WebSocket message handler
```

---

## 🔌 Update the WebSocket Debugger URL

1. Visit this URL in your browser:  
   [http://127.0.0.1:9222/json](http://127.0.0.1:9222/json)

2. Look for the field: `"webSocketDebuggerUrl"`  
   Example:  
   `"webSocketDebuggerUrl": "ws://127.0.0.1:9222/devtools/page/ABC123"`

3. Copy the full WebSocket URL and paste it into the `Water.java` file:

```java
String debugUrl = "ws://127.0.0.1:9222/devtools/page/ABC123";
```

---

## ▶️ Build & Run

From the root of your project directory, compile and run the Java files:

```bash
javac -d out src/*.java
java -cp out Water
```

---

## 📄 Output

- Chrome opens the target page in headless mode.
- Injects your `bundle.js` to manipulate the DOM or behavior.
- Saves the final rendered page as a PDF to your current directory.

---

## 💡 Notes

- Make sure the Chrome process is running before executing `Water.java`.
- You can modify `bundle.js` to alter page styles, add content, or delay rendering.
- You may automate PDF naming, folder output, or target URLs within the Java code.

---

## 📝 License

This project is licensed under the MIT License.  
See `LICENSE` file for more information.
