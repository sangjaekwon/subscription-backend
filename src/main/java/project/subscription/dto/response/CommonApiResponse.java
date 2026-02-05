package project.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    public static <T> CommonApiResponse<T> ok(T data) {
        return new CommonApiResponse<>(true, data, null);
    }
    public static CommonApiResponse<?> error(String message) {
        return new CommonApiResponse<>(false, null, message);
    }

}
