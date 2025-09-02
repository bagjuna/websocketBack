<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <v-card>
          <v-card-title class="text-h5 text-center">로그인</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="doLogin">
              <v-text-field label="이메일"
                            v-model="email"
                            type="email"
                            required></v-text-field>
              <v-text-field
                label="비밀번호"
                v-model="password"
                type="password"
                required
              ></v-text-field>

              <v-btn type="submit" color="primary" block>로그인</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { jwtDecode } from 'jwt-decode'

const router = useRouter()

// ref를 사용해야 함 (reactive가 아닌)
const email = ref('')
const password = ref('')

const doLogin = async () => {
  try {
    const data = {
      email: email.value, // .value 추가
      password: password.value, // .value 추가
    }

    const response = await axios.post('/api/member/doLogin', data)
    const token = response.data.token
    const role = jwtDecode(token).role //
    const sub = jwtDecode(token).sub //

    localStorage.setItem('token', token)
    localStorage.setItem('role', role)
    localStorage.setItem('email', sub)

    // Vue Router 사용 권장
    // router.push('/')
    window.location.href = '/'

    console.log(response.data)
  } catch (error) {
    console.error('로그인 실패:', error)
    // 에러 처리 로직 추가
  }
}


</script>
