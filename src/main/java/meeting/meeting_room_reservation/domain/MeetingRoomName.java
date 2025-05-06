package meeting.meeting_room_reservation.domain;

public enum MeetingRoomName {
    ROOM_A(""),
    ROOM_B(""),
    ROOM_C(""),
    ROOM_D(""),
    ;

    private final String roomCd;

    MeetingRoomName(String roomCd) {
        this.roomCd = roomCd;
    }

    public String getCode() {
        return this.roomCd;
    }
}
