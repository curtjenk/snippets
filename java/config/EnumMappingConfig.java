
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
    This class configures the FormatterRegistry with ready-to-use converters
    appropriate for most Spring Boot applications.  Among these out-of-the-box converters,
    we find StringToEnumIgnoringCaseConverterFactory. As the name implies,
    it converts a string into an enum in a case-insensitive manner.
 */
@Configuration
public class EnumMappingConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        ApplicationConversionService.configure(registry);
    }
}
