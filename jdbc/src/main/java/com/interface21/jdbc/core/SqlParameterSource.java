package com.interface21.jdbc.core;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlParameterSource {

    private static final Logger log = LoggerFactory.getLogger(SqlParameterSource.class);

    private final Object source;

    public SqlParameterSource(final Object source) {
        validateSourceIsNull(source);
        this.source = source;
    }

    private void validateSourceIsNull(final Object source) {
        if (source == null) {
            throw new IllegalArgumentException("SQL 파라미터 소스 객체는 null이 입력될 수 없습니다.");
        }
    }

    public String getParameterValue(final String parameterName) {
        validateParameterName(parameterName);
        final Field parameter = parseFields().stream()
                .filter(field -> field.getName().equals(parameterName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("일치하는 파라미터가 없습니다."));
        return convertStringValue(parameter);
    }

    private void validateParameterName(final String parameterName) {
        if (parameterName == null || parameterName.isBlank()) {
            throw new IllegalArgumentException("파라미터 이름으로 null 혹은 공백이 입력될 수 없습니다.");
        }
    }

    private List<Field> parseFields() {
        final Class<?> clazz = source.getClass();
        return List.of(clazz.getDeclaredFields());
    }

    private String convertStringValue(final Field field) {
        try {
            field.setAccessible(true);
            return String.valueOf(field.get(source));
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
