package cz.qjetta.weatherstation.locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class I18nUtil {

	@Autowired
	MessageSource messageSource;

	@Resource(name = "localeHolder")
	LocaleHolder localeHolder;

	public String getMessage(String code, String... args) {
		return messageSource.getMessage(code, args,
				localeHolder.getCurrentLocale());
	}

}