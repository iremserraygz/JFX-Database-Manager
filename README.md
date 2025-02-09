# JFX Database Manager 🚀

**JFX Database Manager** is a JavaFX application that allows users to connect to MySQL databases, view databases and tables, execute SQL queries, and perform basic table manipulations. This project is developed by [You] & [Leofia] 🤝.

## 📌 Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [Technical Details](#technical-details)

## 🎯 Introduction

JFX Database Manager is designed as a simple and user-friendly database management tool for developers and database administrators. It provides an intuitive desktop alternative to phpMyAdmin, making database operations seamless and accessible.

## ✨ Features

✅ **Database Connection:**
- Connect to MySQL databases using URL, username, and password.
- Automatic transition to the main screen after a successful connection.

✅ **Database & Table Viewing:**
- List all databases on the connected server.
- Display tables for the selected database.

✅ **Table Data Viewing & Editing:**
- View table data dynamically.
- **Column Management:** Add/remove columns with drag-and-drop support.
- **Row Operations:** Add, remove, and update table rows.

✅ **SQL Query Execution:**
- Run `SELECT`, `ALTER`, `CREATE`, `DROP`, and other SQL commands directly in the app.
- View query results instantly.

✅ **Error Handling & User-Friendly UI:**
- Detailed error messages and alerts.
- Modern and clean JavaFX interface.

✅ **Keyboard Shortcuts:**
- Press **Enter** on the login screen to connect instantly.

## 🖼️ Screenshots

### 🔑 Login Screen
![Login Screen](https://github.com/user-attachments/assets/0bdca968-1b96-4bbc-bdef-8048dadfdffa)

### 📊 Main Screen
![Main Screen](https://github.com/user-attachments/assets/ad6e1ef7-81e6-4ff0-82e4-743aa2838de7)

### 🗂️ Table View
![Table View](https://github.com/user-attachments/assets/fb6cd29f-98af-445d-9b78-0214ef9fb9d1)

### 📜 Query Execution
![Query Execution](https://github.com/user-attachments/assets/d9c4e340-391c-4cdb-be08-c08e2c1146e9)

## ⚙️ Installation

1️⃣ **Install JDK 8+** ☕
2️⃣ **Install XAMPP** 🛠️ (Start MySQL module)
3️⃣ **Download the project files** 📥
4️⃣ **Install dependencies** 📦 (via Maven/Gradle)
   - JavaFX
   - MySQL Connector/J
5️⃣ **Open in IntelliJ IDEA or NetBeans** 🖥️

## 🚀 Usage

1️⃣ **Start the application** 🎬
2️⃣ **Login to the database** 🔐 (Enter database URL, username, and password, then hit **Enter** or click "Connect")
3️⃣ **Browse databases & tables** 📁
4️⃣ **Modify table data** ✏️ (Add/Edit/Delete rows & columns)
5️⃣ **Run SQL queries** 📝 (Execute and view results instantly)
6️⃣ **Save or refresh data** 🔄

## 🔧 Technical Details

- **Language:** Java ☕
- **GUI Framework:** JavaFX 🎨
- **Database Connection:** JDBC (MySQL Connector/J) 🔗
- **Architecture:** MVC (Model-View-Controller) 📌
- **Main Classes:**
  - `MainApp` - Application entry point
  - `LoginController` - Manages login UI & authentication
  - `MainController` - Handles main UI & table operations
  - `DatabaseConnector` - Manages database connections
  - `RowData` - Represents table row data as a model

---

💡 **Contributors:** 
[Leofia] ✨  


