package dbbrowser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainController {

    @FXML
    private ListView<String> databaseList;
    @FXML
    private ListView<String> relationList;
    @FXML
    private TableView<RowData> dataTable;
    @FXML
    private Button updateID;
    @FXML
    private Button refreshID;
    @FXML
    private TextArea queryTextArea;
    @FXML
    private Button executeQueryButton;
    private List<RowData> deletedRows = new ArrayList<>();
    private List<String> removedColumns = new ArrayList<>();
    private Connection conn;
    private TextField tableNameField;
    private TextField columnNameField;
    private TextField columnTypeField;

    public void initialize() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "1234");
            conn.setAutoCommit(true);
            loadDatabases();
            setupListeners();
        } catch (SQLException e) {
            showDetailedAlert("Connection Error", "Database connection failed", e);
        }
    }

    private void showDetailedAlert(String title, String message, SQLException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);

        TextArea textArea = new TextArea(e.getMessage());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }

    private void setupListeners() {
        databaseList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadRelations(newVal);
            }
        });

        relationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadRelationData(newVal);
            }
        });
    }

    private void loadDatabases() {
        try {
            ResultSet rs = conn.getMetaData().getCatalogs();
            ObservableList<String> databases = FXCollections.observableArrayList();
            while (rs.next()) {
                databases.add(rs.getString("TABLE_CAT"));
            }
            databaseList.setItems(databases);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load databases: " + e.getMessage());
        }
    }

    private void loadRelations(String database) {
        try {
            conn.setCatalog(database);
            ResultSet rs = conn.getMetaData().getTables(database, null, "%", new String[]{"TABLE"});
            ObservableList<String> relations = FXCollections.observableArrayList();
            while (rs.next()) {
                relations.add(rs.getString("TABLE_NAME"));
            }
            relationList.setItems(relations);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load relations: " + e.getMessage());
        }
    }

    private void loadRelationData(String relation) {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + relation)) {

            ObservableList<RowData> data = FXCollections.observableArrayList();
            dataTable.getColumns().clear();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                final int colIndex = i - 1;
                TableColumn<RowData, String> col = new TableColumn<>(metaData.getColumnName(i));
                col.setCellValueFactory(param -> param.getValue().get(colIndex));
                col.setCellFactory(TextFieldTableCell.forTableColumn());
                dataTable.getColumns().add(col);
            }

            while (rs.next()) {
                RowData row = new RowData(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    row.set(i - 1, rs.getString(i));
                }
                data.add(row);
            }

            dataTable.setItems(data);
            dataTable.setEditable(true);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemoveLastColumn(ActionEvent event) {
        ObservableList<TableColumn<RowData, ?>> columns = dataTable.getColumns();

        if (columns.isEmpty()) {
            showAlert("Error", "No columns to remove.");
            return;
        }

        TableColumn<RowData, ?> lastColumn = columns.get(columns.size() - 1);
        String columnName = lastColumn.getText();
        removedColumns.add(columnName);

        columns.remove(lastColumn);
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        String selectedTable = relationList.getSelectionModel().getSelectedItem();

        if (selectedTable == null) {
            showAlert("Error", "Please select a table.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            for (String columnName : removedColumns) {
                dropColumnFromDatabase(selectedTable, columnName);
            }

            for (RowData row : deletedRows) {
                deleteRowFromDatabase(selectedTable, row);
            }

            for (RowData row : dataTable.getItems()) {
                String primaryKeyValue = row.getValue(0);  

                if (primaryKeyValue == null || primaryKeyValue.isEmpty()) {
                    insertRowIntoDatabase(selectedTable, row);
                } else {
                    updateRowInDatabase(selectedTable, row);
                }
            }

            conn.commit();
            showAlert("Success", "All changes saved successfully.");
            removedColumns.clear();
            deletedRows.clear();
            loadRelationData(selectedTable);

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            showAlert("Error", "Failed to update data: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void dropColumnFromDatabase(String table, String columnName) throws SQLException {
        String alterTableQuery = "ALTER TABLE " + table + " DROP COLUMN " + columnName;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(alterTableQuery);
        }
    }

    private void updateRowInDatabase(String table, RowData row) throws SQLException {
        String primaryKeyColumn = findPrimaryKeyColumn(table);

        if (primaryKeyColumn == null) {
            throw new SQLException("No primary key found for table: " + table);
        }

        String primaryKeyValue = row.getValue(0);
        if (primaryKeyValue == null || primaryKeyValue.isEmpty()) {
            throw new SQLException("Primary key value is missing for row: " + row);
        }

        StringBuilder updateQuery = new StringBuilder("UPDATE " + table + " SET ");
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < dataTable.getColumns().size(); i++) {
            String columnName = dataTable.getColumns().get(i).getText();
            String newValue = row.getValue(i);

            if (!columnName.equalsIgnoreCase(primaryKeyColumn)) {
                updateQuery.append(columnName).append(" = ?, ");
                params.add(newValue);
            }
        }

        updateQuery.setLength(updateQuery.length() - 2);
        updateQuery.append(" WHERE ").append(primaryKeyColumn).append(" = ?");
        params.add(primaryKeyValue);

        try (PreparedStatement pstmt = conn.prepareStatement(updateQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.executeUpdate();
        }
    }

    private void insertRowIntoDatabase(String table, RowData row) throws SQLException {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO " + table + " (");
        StringBuilder valuesPlaceholder = new StringBuilder(" VALUES (");
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < dataTable.getColumns().size(); i++) {
            String columnName = dataTable.getColumns().get(i).getText();
            String newValue = row.getValue(i);

            insertQuery.append(columnName).append(", ");
            valuesPlaceholder.append("?, ");
            params.add(newValue != null ? newValue : "");
        }

        insertQuery.setLength(insertQuery.length() - 2);
        valuesPlaceholder.setLength(valuesPlaceholder.length() - 2);
        insertQuery.append(")").append(valuesPlaceholder).append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.executeUpdate();

            dataTable.getItems().add(row);
            dataTable.refresh();
        }
    }

    private void handleAddColumn(ActionEvent event) {
        String tableName = tableNameField.getText().trim();
        String columnName = columnNameField.getText().trim();
        String columnType = columnTypeField.getText().trim();

        if (tableName.isEmpty() || columnName.isEmpty() || columnType.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        String query = "ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType;
        System.out.println("SQL Query: " + query);

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            showAlert("Success", "Column added successfully.");
        } catch (SQLException e) {
            showAlert("Error", "Failed to add column: " + e.getMessage());
        }
    }

    private String findPrimaryKeyColumn(String table) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, table)) {
            if (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }
        }
        return null;
    }

    @FXML
    private void handleRefresh(ActionEvent event) throws SQLException {
        String selectedDatabase = databaseList.getSelectionModel().getSelectedItem();
        String selectedRelation = relationList.getSelectionModel().getSelectedItem();
        String selectedTable = relationList.getSelectionModel().getSelectedItem();
        for (RowData selectedRow:deletedRows) {
            deletedRows.remove(selectedRow);
               
        }


        for (String columnName : removedColumns) {
            removedColumns.remove(columnName);

        }
        
        
        if (selectedDatabase != null && selectedRelation != null) {
            loadRelationData(selectedRelation);
        } else {
            showAlert("Error", "Please select a database and relation to refresh.");
        }
    }

    @FXML
    private void handleExecuteQuery(ActionEvent event) {
        String query = queryTextArea.getText().trim();

        if (query.isEmpty()) {
            showAlert("Error", "Please enter a SQL query.");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            if (query.toUpperCase().startsWith("ALTER")
                    || query.toUpperCase().startsWith("CREATE")
                    || query.toUpperCase().startsWith("DROP")) {

                stmt.executeUpdate(query);

                String selectedTable = relationList.getSelectionModel().getSelectedItem();
                if (selectedTable != null) {
                    loadRelationData(selectedTable);
                }

                showAlert("Success", "Query executed successfully.");
            } else {
                ResultSet rs = stmt.executeQuery(query);

                ObservableList<RowData> data = FXCollections.observableArrayList();
                dataTable.getColumns().clear();

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    final int colIndex = i - 1;
                    TableColumn<RowData, String> col = new TableColumn<>(metaData.getColumnName(i));
                    col.setCellValueFactory(param -> param.getValue().get(colIndex));
                    col.setCellFactory(TextFieldTableCell.forTableColumn());
                    dataTable.getColumns().add(col);
                }

                while (rs.next()) {
                    RowData row = new RowData(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        row.set(i - 1, rs.getString(i));
                    }
                    data.add(row);
                }

                dataTable.setItems(data);
                showAlert("Success", "Query executed successfully.");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to execute query: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddDynamicColumn(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Column");
        dialog.setHeaderText("Enter Column Name and Type");
        dialog.setContentText("Column Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(columnName -> {
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>("VARCHAR(255)",
                    "VARCHAR(255)", "INT", "DOUBLE", "DATE", "TEXT");
            typeDialog.setTitle("Select Column Type");
            typeDialog.setHeaderText("Select the Column Type");
            typeDialog.setContentText("Column Type:");

            Optional<String> typeResult = typeDialog.showAndWait();
            typeResult.ifPresent(columnType -> {
                String selectedTable = relationList.getSelectionModel().getSelectedItem();

                if (selectedTable == null) {
                    showAlert("Error", "Please select a table first.");
                    return;
                }

                String alterQuery;
                if (columnType.equals("INT") || columnType.equals("DOUBLE")) {
                    alterQuery = "ALTER TABLE " + selectedTable + " ADD COLUMN " + columnName + " " + columnType + " DEFAULT 0";
                } else if (columnType.equals("DATE")) {
                    alterQuery = "ALTER TABLE " + selectedTable + " ADD COLUMN " + columnName + " " + columnType + " DEFAULT CURRENT_DATE";
                } else {
                    alterQuery = "ALTER TABLE " + selectedTable + " ADD COLUMN " + columnName + " " + columnType + " DEFAULT NULL";
                }

                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(alterQuery);

                    loadRelationData(selectedTable);

                    showAlert("Success", "Column added successfully.");
                } catch (SQLException e) {
                    showAlert("Error", "Error occurred while adding column: " + e.getMessage());
                }
            });
        });
    }

    private void deleteRowFromDatabase(String table, RowData row) throws SQLException {
        String primaryKeyColumn = findPrimaryKeyColumn(table);

        if (primaryKeyColumn == null) {
            throw new SQLException("No primary key found for table: " + table);
        }

        String primaryKeyValue = row.getValue(0);
        if (primaryKeyValue == null || primaryKeyValue.isEmpty()) {
            throw new SQLException("Primary key value is missing for row: " + row);
        }

        String deleteQuery = "DELETE FROM " + table + " WHERE " + primaryKeyColumn + " = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, primaryKeyValue);
            pstmt.executeUpdate();
        }
    }

    @FXML
    private void handleRemoveSelectedRow(ActionEvent event) {
        RowData selectedRow = dataTable.getSelectionModel().getSelectedItem();

        if (selectedRow == null) {
            showAlert("Error", "No row selected.");
            return;
        }

        deletedRows.add(selectedRow);

        dataTable.getItems().remove(selectedRow);
    }

    @FXML
    private void handleAddRow(ActionEvent event) {
        String selectedTable = relationList.getSelectionModel().getSelectedItem();

        if (selectedTable == null) {
            showAlert("Error", "Please select a table first.");
            return;
        }

        List<String> columns = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name = '" + selectedTable + "'")) {

            while (rs.next()) {
                columns.add(rs.getString("column_name"));
            }

        } catch (SQLException e) {
            showAlert("Error", "Error occurred while fetching columns: " + e.getMessage());
            return;
        }

        if (columns.isEmpty()) {
            showAlert("Error", "No columns found for the selected table.");
            return;
        }

        Map<String, String> values = new HashMap<>();
        for (String column : columns) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Enter Value for Column");
            dialog.setHeaderText("Enter value for column: " + column);
            dialog.setContentText("Value:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> values.put(column, value));
        }

        if (values.isEmpty()) {
            showAlert("Error", "No values entered for the new row.");
            return;
        }

        StringBuilder insertQuery = new StringBuilder("INSERT INTO ").append(selectedTable).append(" (");
        StringBuilder placeholders = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, String> entry : values.entrySet()) {
            insertQuery.append(entry.getKey()).append(", ");
            placeholders.append("?, ");
            params.add(entry.getValue());
        }

        insertQuery.setLength(insertQuery.length() - 2);
        placeholders.setLength(placeholders.length() - 2);
        insertQuery.append(") VALUES (").append(placeholders).append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            pstmt.executeUpdate();

            loadRelationData(selectedTable);

            showAlert("Success", "Row added successfully.");
        } catch (SQLException e) {
            showAlert("Error", "Error occurred while adding row: " + e.getMessage());
        }
    }

}
