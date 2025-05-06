package meeting.meeting_room_reservation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResvRmResponseDto {

    @Schema(description = "회의실 분류 코드", example = "A101")
    private String roomCd;

    @Schema(description = "회의실명", example = "대회의실")
    private String roomNm;

    @Schema(description = "", example = "")
    private int capacity;
}
