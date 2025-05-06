package meeting.meeting_room_reservation.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResvDto {

    @NotNull
    @Schema(description = "사용자ID", example = "e101010")
    private String userId;

    @NotNull
    @Schema(description = "회의실 분류 코드", example = "A101")
    private String roomCd;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "예약 시작시간", example = "")
    private LocalDateTime startTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "예약 종료시간", example = "")
    private LocalDateTime endTime;
}
