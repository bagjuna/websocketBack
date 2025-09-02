<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title class="text-center text-h5"> 내 채팅 목록 </v-card-title>
          <v-card-text>
            <v-table>
              <v-table>
                <thead>
                  <tr>
                    <th>채팅방 이름</th>
                    <th>읽지 않은 메시지</th>
                    <th>액션</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="chat in chatList" :key="chat.roomId">
                    <td>{{ chat.roomName }}</td>
                    <td>{{ chat.unReadCount }}</td>
                    <td>
                      <v-btn color="primary" @click="enterChatRoom(chat.roomId)">입장</v-btn>
                      <v-btn
                        color="secondary"
                        :disabled="chat.isGroupChat === 'N'"
                        @click="leaveChatRoom(chat.roomId)"
                      >
                        나가기
                      </v-btn>
                    </td>
                  </tr>
                </tbody>
              </v-table>
            </v-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// router 인스턴스
const router = useRouter()

// reactive data
const chatList = ref([])

// lifecycle - created 대신 onMounted 사용
onMounted(async () => {
  const response = await axios.get(`${import.meta.env.VITE_APP_API_BASE_URL}/chat/my/rooms`)
  console.log(response.data)
  chatList.value = response.data
})

// methods
const enterChatRoom = async (roomId) => {
  // 기존의 채팅방 있으면 return 받고, 없으면 새롭게 생성된 roomId로 return
  await router.push(`/chatpage/${roomId}`)
}

const leaveChatRoom = async (roomId) => {
  await axios.delete(`/api/chat/room/group/${roomId}/leave`)
  chatList.value = chatList.value.filter(chat => chat.roomId !== roomId)
}

</script>
