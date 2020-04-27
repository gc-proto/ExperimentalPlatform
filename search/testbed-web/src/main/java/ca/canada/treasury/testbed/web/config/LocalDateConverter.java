package ca.canada.treasury.testbed.web.config;

import java.time.LocalDate;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String timestamp) {
        return LocalDate.parse(timestamp);
    }
}