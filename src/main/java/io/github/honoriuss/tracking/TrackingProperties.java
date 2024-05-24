package io.github.honoriuss.tracking;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tracking.default")
class TrackingProperties {
    private String columnName;

    public String getColumnName() {
        if (columnName == null) {
            return "defaultColumnName";
        }
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
