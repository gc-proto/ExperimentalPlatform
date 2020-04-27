package ca.canada.treasury.testbed.web.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties()
@PropertySource(
    value = "classpath:changelog.yml",
    factory = YamlPropertySourceFactory.class
)
public class ChangeLog {

    private List<LogEntry> logEntries = new ArrayList<>();

    @Bean("changelog")
    @ConfigurationProperties(prefix = "changelog")
    public List<LogEntry> getLogEntries() {
        return logEntries;
    }
    public void setLogEntries(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @Override
    public boolean equals(final Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(
                this, ToStringStyle.SHORT_PREFIX_STYLE)
                .toString();
    }

    public static class LogEntry {

        private LocalDate date;
        private List<PageChanges> pages = new ArrayList<>();
//        private String path;
//        private List<String> changes = new ArrayList<>();

        public LocalDate getDate() {
            return date;
        }
        public void setDate(LocalDate date) {
            this.date = date;
        }


//        public String getPath() {
//            return path;
//        }
//        public void setPath(String path) {
//            this.path = path;
//        }
//
//        public List<String> getChanges() {
//            return changes;
//        }
//        public void setChanges(List<String> changes) {
//            this.changes = changes;
//        }

        public int getChangeCount() {
            int cnt = 0;
            for (PageChanges page : pages) {
                cnt += page.changes.size();
            }
            return cnt;
        }

        public List<PageChanges> getPages() {
            return pages;
        }
        public void setPages(List<PageChanges> pages) {
            this.pages = pages;
        }
        @Override
        public boolean equals(final Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
        @Override
        public String toString() {
            return new ReflectionToStringBuilder(
                    this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .toString();
        }
    }
    public static class PageChanges {
        private String path;
        private List<String> changes = new ArrayList<>();
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public List<String> getChanges() {
            return changes;
        }
        public void setChanges(List<String> changes) {
            this.changes = changes;
        }
        @Override
        public boolean equals(final Object other) {
            return EqualsBuilder.reflectionEquals(this, other);
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
        @Override
        public String toString() {
            return new ReflectionToStringBuilder(
                    this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .toString();
        }
    }
}
