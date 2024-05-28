package fr.ensim.interop.introrest.model.telegram;

import java.util.List;

public class UpdateResponse {
    private List<Update> result;

    public List<Update> getResult() {
        return result;
    }

    public void setResult(List<Update> result) {
        this.result = result;
    }

    public static class Update {
        private Message message;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
    }
}
