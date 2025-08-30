import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MemberCreate from '@/views/MemberCreate.vue'
import LoginPage from '@/views/LoginPage.vue'
import MemberList from '@/views/MemberList.vue'
import SimpleWebSocket from '@/views/SimpleWebSocket.vue'
import StompChatPage from '@/views/StompChatPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/member/create',
      name: 'MemberCreate',
      component: MemberCreate
    },
    {
      path: '/login',
      name: 'LoginPage',
      component: LoginPage
    },
    {
      path: '/member/list',
      name: 'MemberList',
      component: MemberList
    },
    {
      path: '/simple/chat',
      name: 'SimpleWebSocket',
      component : SimpleWebSocket
    },
    {
      path: '/chatPage',
      name: 'StompChatPage',
      component: StompChatPage
    }
  ],
})


export default router
