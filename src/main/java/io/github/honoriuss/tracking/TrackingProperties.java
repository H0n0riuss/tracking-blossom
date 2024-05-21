package io.github.honoriuss.tracking;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tracking.standard")
class TrackingProperties { //TODO mapping optional --> create generic handler?
    private String columnName;

    public String getColumnName() {
        if (columnName == null) {
            columnName = "colName";
        }
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
