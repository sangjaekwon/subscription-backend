package project.subscription.entity;

import jakarta.persistence.AttributeConverter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalDateSetConverter implements AttributeConverter<Set<LocalDate>, String> {
    @Override
    public String convertToDatabaseColumn(Set<LocalDate> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        // 대괄호 없이 "2003-07-17,2003-07-16" 형태로 변환
        return attribute.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<LocalDate> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashSet<>();
        }
        return Arrays.stream(dbData.split(","))
                .map(LocalDate::parse)
                .collect(Collectors.toSet());
    }
}
