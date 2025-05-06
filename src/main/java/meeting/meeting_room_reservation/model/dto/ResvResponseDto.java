package meeting.meeting_room_reservation.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResvResponseDto {

    @Schema(description = "예약 번호", example = "1")
    private Long resvNo;

    @Schema(description = "사용자명", example = "김재원")
    private String userNm;

    @Schema(description = "회의실 분류 코드", example = "A101")
    private String roomCd;

    @Schema(description = "예약 시작시간", example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime startTime;

    @Schema(description = "예약 종료시간", example = "")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime endTime;
}
