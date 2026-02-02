package project.subscription.exception;


import lombok.Getter;

@Getter
public enum ErrorCode {

    DUPLICATE_USERNAME(400, "아이디가 중복되었습니다.");



    private final int status;
    private final String message;
    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
