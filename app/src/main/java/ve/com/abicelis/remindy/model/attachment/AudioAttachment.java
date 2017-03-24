package ve.com.abicelis.remindy.model.attachment;

import ve.com.abicelis.remindy.enums.AttachmentType;

/**
 * Created by abice on 3/3/2017.
 */

public class AudioAttachment extends Attachment {

    private byte[] audio;

    public AudioAttachment(byte[] audio) {
        this.audio = audio;
    }
    public AudioAttachment(int id, int reminderId, byte[] audio) {
        super(id, reminderId);
        this.audio = audio;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.AUDIO;
    }

    public byte[] getAudio() {
        return audio;
    }
    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
