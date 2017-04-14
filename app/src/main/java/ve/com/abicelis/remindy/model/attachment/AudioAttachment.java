package ve.com.abicelis.remindy.model.attachment;

import ve.com.abicelis.remindy.enums.AttachmentType;

/**
 * Created by abice on 3/3/2017.
 */

public class AudioAttachment extends Attachment {

    private String audioFilename;

    public AudioAttachment() {      //Parameterless constructor for audio attachment creation
    }
    public AudioAttachment(String audioFilename) {
        this.audioFilename = audioFilename;
    }
    public AudioAttachment(int id, int reminderId, String audioFilename) {
        super(id, reminderId);
        this.audioFilename = audioFilename;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.AUDIO;
    }

    public String getAudioFilename() {
        return audioFilename;
    }
    public void setAudioFilename(String audioFilename) {
        this.audioFilename = audioFilename;
    }
}
