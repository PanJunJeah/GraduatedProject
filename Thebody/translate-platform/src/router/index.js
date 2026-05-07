import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const routes = [
    {
        path: '/',
        redirect: '/translate',
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { requiresGuest: true },
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue'),
        meta: { requiresGuest: true },
    },
    {
        path: '/translate',
        name: 'Translate',
        component: () => import('../views/Translate.vue'),
        meta: { requiresAuth: true },
    },
    {
        path: '/translate/batch',
        name: 'BatchTranslate',
        component: () => import('../views/BatchTranslate.vue'),
        meta: { requiresAuth: true },
    },
    {
        path: '/history',
        name: 'History',
        component: () => import('../views/History.vue'),
        meta: { requiresAuth: true },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isLoggedIn) {
        next('/login');
    } else if (to.meta.requiresGuest && authStore.isLoggedIn) {
        next('/translate');
    } else {
        next();
    }
});

export default router;
