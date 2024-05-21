package fr.ensim.interop.introrest.model.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author Ruben Bermudez
 * @version 5.1
 * This object represents changes in the status of a chat member.
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberUpdated implements BotApiObject {
    private static final String CHAT_FIELD = "chat";
    private static final String FROM_FIELD = "from";
    private static final String DATE_FIELD = "date";
    private static final String OLDCHATMEMBER_FIELD = "old_chat_member";
    private static final String NEWCHATMEMBER_FIELD = "new_chat_member";
    private static final String INVITELINK_FIELD = "invite_link";

    @JsonProperty(CHAT_FIELD)
    private Chat chat; ///< Chat the user belongs to
    @JsonProperty(FROM_FIELD)
    private User from; ///< Performer of the action, which resulted in the change
    @JsonProperty(DATE_FIELD)
    private Integer date; ///< Date the change was done in Unix time
    @JsonProperty(INVITELINK_FIELD)
    private ChatInviteLink inviteLink; ///< Optional. Chat invite link, which was used by the user to join the chat; for joining by invite link events only.

}
