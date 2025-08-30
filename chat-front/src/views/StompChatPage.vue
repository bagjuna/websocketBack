<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client/dist/sockjs'
import { onBeforeRouteLeave } from 'vue-router'
// import axios from 'axios';


const messages = ref([])
const newMessage = ref('')
const stompClient = ref(null)
const token = ref('')
const senderEmail = ref('')
const connectWebSocket = () => {
  if(stompClient.value && stompClient.value.connected) {
    return
  }
  // stompClient 연결 로직
  // sockjs는 websocket을 내장한 js 라이브러리. http 엔드포인트 사용
  const sockJs = new SockJS(`${import.meta.env.VITE_APP_API_BASE_URL}/connect`)
  stompClient.value = Stomp.over(sockJs)
  token.value = localStorage.getItem('token')
  stompClient.value.connect({
    Authorization: `Bearer ${token.value}`
    },
    () =>{
      stompClient.value.subscribe(`/topic/1`, (message) =>{
        const parseMessage = JSON.parse(message.body)
        messages.value.push(parseMessage)
        scrollToBottom()
      })
    }
  )
}

// 메시지 전송
const sendMessage = () => {
  if (newMessage.value.trim() === '') return
  const message = {
    senderEmail: senderEmail.value,
    message: newMessage.value
  }
  stompClient.value.send(`/publish/1`,  JSON.stringify(message) )
  newMessage.value = ''
}

// 스크롤 처리
const scrollToBottom = () => {
  nextTick(() => {
    const chatBox = document.querySelector('.chat-box')
    if (chatBox) {
      chatBox.scrollTop = chatBox.scrollHeight
    }
  })
}

// WebSocket 연결 해제
const disconnectWebSocket = () => {
  if(stompClient.value && stompClient.value.connected) {
    stompClient.value.unsubscribe(`/topic/1`);
    stompClient.value.disconnect();
  }
}

// lifecycle hooks
onMounted(() => {
  senderEmail.value = localStorage.getItem('email')
  connectWebSocket()
})


// 화면을 완전히 꺼버렸을 때
onBeforeUnmount(() => {
  disconnectWebSocket()
})

// 사용자가 현재 라우트로 이동하려고 할때 호출되는 함수
onBeforeRouteLeave((to, from, next) => {
  disconnectWebSocket()
  next();
})
</script>

<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title class="text-h5 text-center">채팅</v-card-title>
          <v-card-text>
            <div class="chat-box">
              <div v-for="(message, index) in messages" :key="index" class="message"
              :class="[ 'chat-message', message.senderEmail.value === senderEmail.value ? 'sent' : 'received']"
                >
                <strong>{{message.senderEmail.value}}</strong> {{ message.message.value }}
              </div>
            </div>
            <v-text-field v-model="newMessage" label="메시지 입력" @keyup.enter="sendMessage" />
            <v-btn @click="sendMessage" color="primary" block>전송</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.chat-box {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  margin-bottom: 10px;
}
.chat-message {
  margin: 10px;
}
.sent{
  text-align: right;
}
.received{
  text-align: left;
}
</style>
