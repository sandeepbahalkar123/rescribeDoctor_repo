package com.rescribe.doctor.model.patient.delete_notes_record;

import com.rescribe.doctor.interfaces.CustomResponse;

import java.util.ArrayList;

/**
 * Created by riteshpandhurkar on 5/4/18.
 */


public class DeleteNotesReqModel implements CustomResponse {

    private ArrayList<NotesData> notesDetails = new ArrayList<>();

    public ArrayList<NotesData> getNotesDetails() {
        return notesDetails;
    }

    public void setNotesDetails(ArrayList<NotesData> attachmentDetails) {
        this.notesDetails = attachmentDetails;
    }

    public static class NotesData {

        private String id;

        private String fileName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
