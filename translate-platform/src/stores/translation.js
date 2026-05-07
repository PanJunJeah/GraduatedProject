import { defineStore } from 'pinia';
import { translationAPI } from '../api';

export const useTranslationStore = defineStore('translation', {
    state: () => ({
        sourceText: '',
        translatedText: '',
        sourceLang: 'zh',
        targetLang: 'en',
        isTranslating: false,
        history: [],
        isLoadingHistory: false,
    }),

    getters: {
        getSourceText: (state) => state.sourceText,
        getTranslatedText: (state) => state.translatedText,
        getSourceLang: (state) => state.sourceLang,
        getTargetLang: (state) => state.targetLang,
        isTranslating: (state) => state.isTranslating,
        getHistory: (state) => state.history,
    },

    actions: {
        setSourceText(text) {
            this.sourceText = text;
        },

        setSourceLang(lang) {
            this.sourceLang = lang;
        },

        setTargetLang(lang) {
            this.targetLang = lang;
        },

        async translate() {
            if (!this.sourceText.trim()) {
                return { success: false, message: 'Please enter text to translate' };
            }

            this.isTranslating = true;
            try {
                const response = await translationAPI.translate({
                    sourceText: this.sourceText,
                    sourceLang: this.sourceLang,
                    targetLang: this.targetLang,
                });

                if (response.success) {
                    this.translatedText = response.data.translatedText;
                    return { success: true };
                }
            } catch (error) {
                return { success: false, message: error.response?.data?.message || 'Translation failed' };
            } finally {
                this.isTranslating = false;
            }
        },

        async loadHistory() {
            this.isLoadingHistory = true;
            try {
                const response = await translationAPI.getHistory();
                if (response.success) {
                    this.history = response.data;
                }
            } catch (error) {
                console.error('Failed to load history:', error);
            } finally {
                this.isLoadingHistory = false;
            }
        },

        swapLanguages() {
            const tempLang = this.sourceLang;
            this.sourceLang = this.targetLang;
            this.targetLang = tempLang;

            const tempText = this.sourceText;
            this.sourceText = this.translatedText;
            this.translatedText = tempText;
        },

        clear() {
            this.sourceText = '';
            this.translatedText = '';
        },
    },
});
