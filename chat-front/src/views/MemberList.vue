<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title class="text-center text-h5">
            회원 목록
          </v-card-title>
          <v-card-text>
            <v-table>
              <v-table>
                <thead>
                <tr>
                  <th>ID</th>
                  <th>이름</th>
                  <th>email</th>
                  <th>채팅</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="member in memberList" :key="member.id">
                  <td>{{ member.id }}</td>
                  <td>{{ member.name }}</td>
                  <td>{{ member.email }}</td>
                  <td>
                    <v-btn @click="startChat(member.id)">채팅하기</v-btn>
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
const memberList = ref([])

// lifecycle - created 대신 onMounted 사용
onMounted(async () => {
  const response = await axios.get('/api/member/list')
  console.log(response.data)
  memberList.value = response.data
})

// methods
const startChat = async (otherMemberId) => {
  // 기존의 채팅방 있으면 return 받고, 없으면 새롭게 생성된 roomId로 return
  const response = await axios.post(
    `${import.meta.env.VITE_APP_API_BASE_URL}/chat/room/private/create?otherMemberId=${otherMemberId}`
  )
  const roomId = response.data
  router.push(`/chatpage/${roomId}`)
}
</script>
