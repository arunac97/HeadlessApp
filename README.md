===============================================
 Headless Chrome PDF Generator - Java Edition
===============================================

This project automates a headless Chrome session using Java and WebSockets
to load a web page, inject custom JavaScript (bundle.js), and save the result
as a PDF file.

---------------------
  REQUIREMENTS
---------------------
- Java 8 or higher
- Google Chrome installed
- bundle.js file (your custom JavaScript)
- Terminal/command-line access

---------------------
  STEP 1: START CHROME
---------------------
Start a headless Chrome instance with remote debugging enabled.

Paste the following in your terminal (single line or backslash-separated):

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

(Note: use "\" instead of "^" if you're on Unix/Linux/Mac.)

----------------------------
  STEP 2: SET UP WORKSPACE
----------------------------

Create the following folder structure:

your-project/
├── bundle.js
└── src/
    ├── Water.java
    └── ChromeWebSocketListener.java

Make sure `bundle.js` is in the correct location.

----------------------------------
  STEP 3: GET WEBSOCKET DEBUG URL
----------------------------------

1. Open this URL in your browser:
   http://127.0.0.1:9222/json

2. Find the line that looks like:
   "webSocketDebuggerUrl": "ws://127.0.0.1:9222/devtools/page/XXXXXXXXXXXX"

3. Copy that URL.

4. Open Water.java and replace the WebSocket URL with the one you copied:

   Example:
   String debugUrl = "ws://127.0.0.1:9222/devtools/page/XXXXXXXXXXXX";

--------------------------
  STEP 4: RUN THE PROGRAM
--------------------------

Compile and run:

javac -d out src/*.java
java -cp out Water

---------------------------
  OUTPUT
---------------------------

The program will:
1. Open the target webpage
2. Inject bundle.js
3. Save the final rendered page as a PDF

The output PDF will be saved to your working directory.

---------------------------
  LICENSE
---------------------------

MIT License. Use freely and modify as needed.

---------------------------
  NOTES
---------------------------

- Ensure Chrome is running with the correct flags before launching the Java program.
- You can edit bundle.js to change what is injected into the page.
- For automation or bulk processing, consider scripting WebSocket URL discovery.

