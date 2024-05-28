package fr.ensim.interop.introrest.model.telegram;

public class TelegramRequest {
        private String text;
        private String chatId;

// Getters et setters

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
    }

