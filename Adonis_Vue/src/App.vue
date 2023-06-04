<template>
	<div id="app">
		<ChatRoom />
	</div>
</template>

<script>
import ChatRoom from './components/ChatRoom.vue'

export default {
	name: 'App',
	components: {
		ChatRoom
	},
}
</script>

<style>
#app {
	display: flex;
	justify-content: center;
}
</style>
