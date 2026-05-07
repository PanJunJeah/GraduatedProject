import { defineStore } from 'pinia';
import { authAPI } from '../api';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        user: JSON.parse(localStorage.getItem('user') || 'null'),
        isAuthenticated: !!localStorage.getItem('token'),
    }),

    getters: {
        getToken: (state) => state.token,
        getUser: (state) => state.user,
        isLoggedIn: (state) => state.isAuthenticated,
    },

    actions: {
        async login(credentials) {
            try {
                const response = await authAPI.login(credentials);
                if (response.success) {
                    this.token = response.data.token;
                    this.user = {
                        id: response.data.userId,
                        username: response.data.username,
                        email: response.data.email,
                    };
                    this.isAuthenticated = true;
                    localStorage.setItem('token', this.token);
                    localStorage.setItem('user', JSON.stringify(this.user));
                    return { success: true };
                }
            } catch (error) {
                return { success: false, message: error.response?.data?.message || 'Login failed' };
            }
        },

        async register(data) {
            try {
                const response = await authAPI.register(data);
                if (response.success) {
                    this.token = response.data.token;
                    this.user = {
                        id: response.data.userId,
                        username: response.data.username,
                        email: response.data.email,
                    };
                    this.isAuthenticated = true;
                    localStorage.setItem('token', this.token);
                    localStorage.setItem('user', JSON.stringify(this.user));
                    return { success: true };
                }
            } catch (error) {
                return { success: false, message: error.response?.data?.message || 'Registration failed' };
            }
        },

        logout() {
            this.token = null;
            this.user = null;
            this.isAuthenticated = false;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        },
    },
});
