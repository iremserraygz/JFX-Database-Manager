# JavaFX Database Browser Application

This project is a JavaFX application that allows users to connect to MySQL databases, view databases, tables, execute SQL queries, and perform basic table manipulation operations (add/remove columns, add/remove/update rows). This application is similar to phpMyAdmin, offering a desktop alternative for basic database management.

## Table of Contents

-   [Introduction](#introduction)
-   [Features](#features)
-   [Screenshots](#screenshots)
-   [Installation](#installation)
-   [Usage](#usage)
-   [Technical Details](#technical-details)


## Introduction

This application is designed as a simple and user-friendly database management tool for database administrators and developers. Its purpose is to provide an interface where you can easily perform basic database operations, similar to phpMyAdmin, but as a desktop application.

## Features

*   **Database Connection:**
    *   Connect to MySQL databases using URL, username, and password.
    *   Automatic transition to the main screen after a successful connection.
*   **Database and Table Viewing:**
    *   List all databases in the connected database server.
    *   List all tables (relations) in the selected database.
*   **Table Data Viewing:**
    *   View data from the selected table in a table format.
    *   Dynamically display column headers and row data.
*   **Basic Table Editing:**
    *   **Column Addition/Removal:** Add and remove columns from tables dynamically.
         *  Users can also drag and drop a column to the end of the table to remove it. The application will then delete this column from the table
    *   **Row Addition/Removal/Update:** Add new rows, delete selected rows, and edit existing rows in tables.
*   **SQL Query Execution:**
    *   Execute SQL queries within the application and view the results.
    *   Run various queries such as `SELECT`, `ALTER`, `CREATE`, and `DROP`.
*   **Error Handling:**
    *   Display detailed error messages during connection, query, and other operations.
    *   Provide feedback with user-friendly alert messages.
*   **Keyboard Shortcuts:**
    *   Connect in the login screen with the **Enter** key.
*   **User Interface:**
    *   Modern and user-friendly JavaFX interface.
    *   Easy-to-use and understandable menus.

## Screenshots

-   **Login Screen:**
  
    ![Screenshot 2025-01-29 001840](https://github.com/user-attachments/assets/0bdca968-1b96-4bbc-bdef-8048dadfdffa)


-   **Main Screen:**
   
![image](https://github.com/user-attachments/assets/ad6e1ef7-81e6-4ff0-82e4-743aa2838de7)


-   **Table View:**
    ![image](https://github.com/user-attachments/assets/fb6cd29f-98af-445d-9b78-0214ef9fb9d1)


-   **Query Execution:**
    ![image](https://github.com/user-attachments/assets/d9c4e340-391c-4cdb-be08-c08e2c1146e9)



## Installation

1.  **Java Development Kit (JDK):** Ensure you have JDK 8 or later installed on your computer.
2.  **XAMPP:**
    *   Install XAMPP to manage your local MySQL database.
    *   Start the MySQL module from the XAMPP control panel. This will make the database server accessible to the application.
3.  **MySQL Database:**
     * Ensure your MySQL database server is running through XAMPP.
4.  **Project Files:** Download or clone the source code of the project.
5.  **Required Libraries:**
    *   Install the libraries used in the project with a tool that manages project dependencies (e.g., Maven or Gradle).
    *   The required libraries for this project are:
        *   JavaFX
        *   MySQL Connector/J: This library allows the application to connect to the MySQL database.
6.  **IDE:** You can use IntelliJ IDEA or a similar IDE to open the project.

## Usage

1.  **Start the Application:**
    *   Open the project in your IDE or run the compiled JAR file.
2.  **Login Screen:**
    *   Enter the database URL (e.g., `jdbc:mysql://localhost:3306/your_database`), your username, and password. Note: If using XAMPP, the default username for MySQL is typically `root`, with no password.
    *   Click the "Connect" button or press the **Enter** key to connect.
3.  **Main Screen:**
    *   Select a database from the list on the left.
    *   Select a table (relation) from the list that appears for the selected database.
    *   The table data will be displayed in the center area.
4.  **Data Editing:**
    *   Edit the data in the table.
    *   Click the "Add Row" button to add a new row.
    *   Select a row and click the "Remove Selected Row" button to delete a row.
    *   Click the "Add Column" button to add a new column.
    *   Click the "Remove Last Column" button to remove a column.
5.  **SQL Query Execution:**
    *   Enter your SQL queries in the text area at the bottom.
    *   Click the "Execute Query" button to run the query.
6.  **Save Data:**
    *   Click the "Update" button to save the changes.
    *   Click the "Refresh" button to reload the table data.

## Technical Details

*   **Programming Language:** Java
*   **GUI Library:** JavaFX
*   **Database Connection:** JDBC (MySQL Connector/J)
*   **Model-View-Controller (MVC) Architecture:** Forms the basic structure of the application, ensuring that the code is organized and easy to manage.
*   **Classes:**
    *   `MainApp`: Starts the JavaFX application.
    *   `LoginController`: Manages the login screen.
    *   `MainController`: Manages the main application and table operations.
    *   `DatabaseConnector`: Manages database connections.
    *   `RowData`: Represents table row data as a model.
