<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client/dist/sockjs'
import axios from 'axios';


const messages = ref([])
const newMessage = ref('')
const stompClient = ref(null)

const connectWebSocket = () => {
  // stompClient 연결 로직
  const sockJs = new SockJS(`${import.meta.env.VITE_APP_API_BASE_URL}/connect`)
  stompClient.value = Stomp.over(sockJs)

  stompClient.value.connect(
    () =>{
      stompClient.value.subscribe(`/topic/1`, (message) =>{
        this.messages.push(message.data)
        scrollToBottom()
      })
    }
  )
}

// 메시지 전송
const sendMessage = () => {
  if (newMessage.value.trim() === '') return
  stompClient.value.send(`/publish/1`, JSON.stringify({ message: newMessage.value }))
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
}

// lifecycle hooks
onMounted(() => {
  connectWebSocket()
})

onBeforeUnmount(() => {
  disconnectWebSocket()
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
              <div v-for="(message, index) in messages" :key="index" class="message">
                {{ message }}
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
</style>
