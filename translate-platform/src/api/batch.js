export const batchAPI = {
    batchTranslate: (data) => apiClient.post('/translation/batch', data),
};
