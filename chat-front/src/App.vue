<script setup>
import HeaderComponent from './components/HeaderComponent.vue';
</script>

<template>
  <v-app>
    <HeaderComponent />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>
