package project.subscription.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponse<T> {

    @Schema(description = "현재 페이지의 데이터 목록")
    private List<T> content;

    @Schema(description = "전체 데이터 수", example = "25")
    private long totalElements;
    @Schema(description = "전체 페이지 수", example = "3")
    private int totalPages;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;
    @Schema(description = "현재 페이지 번호", example = "0")
    private int pageNumber;
    @Schema(description = "현재 페이지 데이터 수", example = "10")
    private int numberOfElements;


}
