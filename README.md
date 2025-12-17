
# 🍽️ Distributed Restaurant POS System (Java Socket)

**分散式餐飲管理系統**

這是一個基於 **Java Swing** 與 **Socket Networking** 開發的 C/S 架構餐飲管理系統。模擬了餐廳外場（前台 POS）與內場（廚房 KDS）之間的即時資訊傳輸與狀態同步。

---

## 📖 專案簡介 (Overview)

本專案旨在解決餐廳內外場資訊不同步的問題。透過 TCP/IP 網路協定，將前台的點餐資訊即時傳送至後台廚房。

系統分為兩個主要部分：

1. **Client (前台)**：提供服務生進行場地佈置、座位管理與點餐。
2. **Server (後台)**：提供廚房人員接收訂單、監控前台連線狀態。

---

## ✨ 核心功能 (Key Features)

### 🖥️ 前台端 (Client - POS)

* **動態場地佈置**：支援動態新增桌子 (Table)，並可透過滑鼠拖曳 (Drag & Drop) 手動調整桌位佈局。
* **權限控管**：具備「管理員模式」開關，防止誤觸新增或移動功能。
* **即時點餐**：直覺的圖形化菜單介面，支援購物車預覽與一鍵送單。
* **自動連線**：程式啟動時自動尋找伺服器，並建立長連線。

### 🍳 後台端 (Server - Kitchen)

* **連線狀態監控**：
* 利用 Keep-Alive 機制即時偵測前台狀態。
* 🟢 **綠燈**：前台已連線 / 🔴 **紅燈**：前台斷線。


* **訂單即時顯示**：接收來自前台的 `Order` 物件，並將內容即時顯示於看板上。
* **音效提示**：收到新訂單時播放提示音效（或是系統嗶聲），提醒廚房人員。

---

## 🛠️ 技術堆疊 (Tech Stack)

* **語言**：Java 8+
* **GUI 框架**：Java Swing (JFrame, JPanel, JDialog)
* **繪圖技術**：Java 2D Graphics (Custom Painting for Tables)
* **網路通訊**：Java Network (Socket, ServerSocket, TCP/IP)
* **資料傳輸**：Object Serialization (實作 `Serializable` 介面)
* **多執行緒**：Multi-threading (分離 UI 與網路監聽執行緒)

---

## 📂 專案結構 (Project Structure)

```text
src
├── 📦 common           // [共用] 資料物件 (Data Models)
│   ├── Order.java      // 訂單物件 (實作 Serializable)
│   ├── Product.java    // 商品物件
│   └── Table.java      // 桌子物件
│
├── 📦 server           // [後台] 廚房端程式
│   └── KitchenServerFrame.java
│
├── 📦 client           // [前台] POS端程式
│   ├── PosClientFrame.java  // 主視窗
│   ├── RestaurantPanel.java // 繪圖畫布 (處理滑鼠拖曳)
│   └── OrderDialog.java     // 點餐視窗
│
└── 📦 main             // [啟動] 程式入口
    └── AppLauncher.java

```

---

## 🚀 如何執行 (How to Run)

由於本專案模擬分散式架構，**請務必執行兩次程式**以模擬兩台不同的電腦。

1. **編譯專案** (Compile the project).
2. **啟動後台 (Server)**：
* 執行 `AppLauncher`，選擇 **"啟動 Server"**。
* 視窗會自動靠向螢幕 **右側**。
* 初始狀態顯示紅色「等待連線...」。


3. **啟動前台 (Client)**：
* 再次執行 `AppLauncher`，選擇 **"啟動 Client"**。
* 視窗會自動靠向螢幕 **左側**。
* 此時 Server 端狀態應轉為綠色「前台已連線」。



---

## 💡 系統設計細節 (System Design)

### 1. 網路通訊架構

本系統使用 **Java Socket** 進行雙向通訊：

* **狀態連線 (Status Connection)**：Client 啟動時建立一條長連線，發送 `LOGIN` 訊號。Server 透過持續監聽此連線來判斷 Client 是否存活 (Heartbeat)。
* **資料傳輸 (Data Transmission)**：訂單資料封裝為 `Order` 物件，透過 `ObjectOutputStream` 序列化後在網路傳輸，確保資料結構完整。

### 2. 多執行緒 (Concurrency)

為避免網路 I/O 阻塞 (Blocking) 導致圖形介面凍結，Server 端採用多執行緒設計：

* **Main Thread**：負責 Swing GUI 繪製。
* **Server Thread**：負責 `ServerSocket.accept()` 監聽連線。
* **Client Handler Thread**：負責處理個別客戶端的資料讀取。

### 3. 客製化繪圖 (Custom Painting)

桌子的顯示並非使用標準按鈕元件，而是覆寫 `JPanel` 的 `paintComponent` 方法，利用 `Graphics` 類別繪製矩形與文字。這提供了更高的客製化彈性（如：未來可繪製圓形桌或加入人數圖示）。

---

## 🔮 未來展望 (Future Roadmap)

* [ ] **資料庫串接**：將菜單與營收紀錄存入 MySQL/SQLite 資料庫。
* [ ] **碰撞偵測演算法**：實作矩形重疊檢查，防止桌子在拖曳時重疊。
* [ ] **歷史報表**：新增後台功能，可查詢歷史訂單並匯出 Excel。

---

## 👤 作者 (Author)

* **開發者**：[你的名字]
* **學號/單位**：[你的學號]
* **專案性質**：Java 進階程式設計期末專案
