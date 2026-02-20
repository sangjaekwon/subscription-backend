package project.subscription.exception.ex;


import lombok.Getter;


@Getter
public enum ErrorCode {

    DUPLICATE_USERNAME(400, "아이디가 중복되었습니다."),

    EXPIRED_TOKEN(401, "토큰이 만료되었습니다. 다시 로그인 해 주세요."),
    INVALID_TOKEN(401, "토큰이 유효하지 않습니다."),

    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다."),
    SUBSCRIPTION_NOT_FOUND(404, "구독 정보를 찾을 수 없습니다."),

    EXPIRED_EMAILCODE(410, "인증 코드가 만료되었습니다."),
    INVALID_EMAILCODE(400, "인증 코드가 알맞지 않습니다."),
    MAIL_SEND_FAILED(503, "메일 전송에 실패하였습니다.");


    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
