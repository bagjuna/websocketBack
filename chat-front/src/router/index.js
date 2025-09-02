import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import MemberCreate from '@/views/MemberCreate.vue'
import LoginPage from '@/views/LoginPage.vue'
import MemberList from '@/views/MemberList.vue'
import SimpleWebSocket from '@/views/SimpleWebSocket.vue'
import StompChatPage from '@/views/StompChatPage.vue'
import GroupChattingList from '@/views/GroupChattingList.vue'
import MyChatPage from '@/views/MyChatPage.vue'

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
      path: '/chatPage/:roomId',
      name: 'StompChatPage',
      component: StompChatPage,
      props: true
    },
    {
      path: '/groupchatting/list',
      name: 'GroupChattingList',
      component: GroupChattingList
    },
    {
      path: '/my/chat/page',
      name: 'MyChatPage',
      component: MyChatPage
    },
  ],
})


export default router
