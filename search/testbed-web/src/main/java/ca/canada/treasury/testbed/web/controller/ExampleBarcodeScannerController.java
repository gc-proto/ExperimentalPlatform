package ca.canada.treasury.testbed.web.controller;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExampleBarcodeScannerController {

    public static final String DEFAULT_DECODER = "upc_reader";

    @GetMapping(value="/example/barcodescanner")
    public String search(Model model, BarcodeConfig barcodeConfig) {
        if (StringUtils.isBlank(barcodeConfig.getDecoder())) {
            barcodeConfig.setDecoder(DEFAULT_DECODER);
        }
        model.addAttribute("barcodeConfig", barcodeConfig);
        return "example-barcodescanner";
    }

    public static class BarcodeConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        private String decoder;
        public String getDecoder() {
            return decoder;
        }
        public void setDecoder(String decoder) {
            this.decoder = decoder;
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
                    this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
        }
    }

}