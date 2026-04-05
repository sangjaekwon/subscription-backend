package project.subscription.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
    @Schema(description = "성공 시 반환 데이터")
    private T data;
    @Schema(description = "실패 시 에러 메시지", example = "Access 토큰이 유효하지 않습니다.")
    private String error;

    public static <T> CommonApiResponse<T> ok(T data) {
        return new CommonApiResponse<>(true, data, null);
    }
    public static CommonApiResponse<?> error(String message) {
        return new CommonApiResponse<>(false, null, message);
    }

}
