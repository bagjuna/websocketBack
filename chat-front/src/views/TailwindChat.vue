<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import Stomp from 'webstomp-client';
import SockJS from 'sockjs-client/dist/sockjs'
import { onBeforeRouteLeave } from 'vue-router'
import axios from 'axios'


const messages = ref([])
const newMessage = ref('')
const stompClient = ref(null)
const token = ref('')
const senderEmail = ref('')
const roomId = ref(1) // 임시로 1번 방



const connectWebSocket = () => {
  if(stompClient.value && stompClient.value.connected) {
    return
  }
  // stompClient 연결 로직
  // sockjs는 websocket을 내장한 js 라이브러리. http 엔드포인트 사용
  const sockJs = new SockJS(`${import.meta.env.VITE_APP_API_BASE_URL}/connect`)
  stompClient.value = Stomp.over(sockJs)
  console.log(stompClient.value)
  token.value = localStorage.getItem('token')
  stompClient.value.connect({
      Authorization: `Bearer ${token.value}`
    },
    () =>{
      stompClient.value.subscribe(`/topic/${roomId.value}`, (message) =>{
        const parseMessage = JSON.parse(message.body)
        messages.value.push(parseMessage)
        scrollToBottom()
      },{Authorization: `Bearer ${token.value}`} )
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
  stompClient.value.send(`/publish/${roomId.value}`,  JSON.stringify(message) )
  newMessage.value = ''
}

// 스크롤 처리
const scrollToBottom = () => {
  nextTick(() => {
    // JS가 참조할 수 있도록 `chat-box` 클래스는 유지합니다.
    const chatBox = document.querySelector('.chat-box')
    if (chatBox) {
      chatBox.scrollTop = chatBox.scrollHeight
    }
  })
}

// WebSocket 연결 해제
const disconnectWebSocket = async () => {
  // 채팅방 나갈때 읽음 처리
  await axios.post(`${import.meta.env.VITE_APP_API_BASE_URL}/chat/room/${roomId.value}/read`)

  if(stompClient.value && stompClient.value.connected) {
    stompClient.value.unsubscribe(`/topic/${roomId.value}`);
    stompClient.value.disconnect();
  }
}

// lifecycle hooks
onMounted(async() => {
  senderEmail.value = localStorage.getItem('email')
  const response = await axios.get(`${import.meta.env.VITE_APP_API_BASE_URL}/chat/history/${roomId.value}`)
  console.log(response.data)
  messages.value = response.data
  connectWebSocket()
  scrollToBottom() // 초기 로딩 후에도 스크롤을 맨 아래로 이동
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
  <div class="flex justify-center items-center min-h-screen bg-gray-100 p-4">
    <div class="w-full max-w-2xl bg-white rounded-lg shadow-xl">
      <div class="p-4 border-b">
        <h1 class="text-2xl font-bold text-center text-gray-800">채팅</h1>
      </div>

      <div class="p-4">
        <div class="chat-box h-80 overflow-y-auto p-4 bg-gray-50 rounded-md space-y-4">
          <div v-for="(message, index) in messages" :key="index"
               class="flex"
               :class="message.senderEmail === senderEmail ? 'justify-end' : 'justify-start'">
            <div class="max-w-xs lg:max-w-md p-3 rounded-lg"
                 :class="{
                   'bg-blue-500 text-white': message.senderEmail === senderEmail,
                   'bg-gray-200 text-gray-800': message.senderEmail !== senderEmail
                 }">
              <strong class="block text-sm font-semibold">{{ message.senderEmail }}</strong>
              <p class="text-base">{{ message.message }}</p>
            </div>
          </div>
        </div>

        <div class="mt-4">
          <input
            v-model="newMessage"
            type="text"
            placeholder="메시지를 입력하세요..."
            @keyup.enter="sendMessage"
            class="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            @click="sendMessage"
            class="w-full mt-2 px-4 py-2 bg-blue-600 text-white font-semibold rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 transition duration-200"
          >
            전송
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Tailwind CSS 유틸리티 클래스가 모든 스타일을 처리하므로 이 섹션은 비워도 됩니다. */
/* 스크롤바 스타일링을 추가하고 싶다면 여기에 작성할 수 있습니다. */
.chat-box::-webkit-scrollbar {
  width: 8px;
}

.chat-box::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-box::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.chat-box::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
