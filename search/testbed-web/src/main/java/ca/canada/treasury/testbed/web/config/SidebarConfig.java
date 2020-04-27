package ca.canada.treasury.testbed.web.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.ListOrderedMap;
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
@EnableConfigurationProperties
@PropertySource(
    value = "classpath:sidebar-config.yml",
    factory = YamlPropertySourceFactory.class
)
@ConfigurationProperties()
public class SidebarConfig {

    private List<MenuItem> menu = new ArrayList<>();

    @Bean("menu")
    public List<MenuItem> getMenu() {
        return menu;
    }
    public void setMenu(List<MenuItem> menu) {
        this.menu = menu;
    }

    @Bean("pathLabels")
    public Map<String, String> getPathLabels() {
        //TODO make recursive if we want to support more than 1 level deep
        ListOrderedMap<String, String> map = new ListOrderedMap<>();
        for (MenuItem item : menu) {
            if (CollectionUtils.isEmpty(item.submenus)) {
                map.put(item.path, item.label);
            } else {
                for (MenuItem subitem : item.submenus) {
                    map.put(item.path + subitem.path, subitem.label);
                }
            }
        }
        return map;
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

    public static class MenuItem {
        private String label;
        private String path;
        private String icon;
        private List<MenuItem> submenus = new ArrayList<>();

        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
        public String getPath() {
            return path;
        }
        public void setPath(String path) {
            this.path = path;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public List<MenuItem> getSubmenus() {
            return submenus;
        }
        public void setSubmenus(List<MenuItem> submenus) {
            this.submenus = submenus;
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
