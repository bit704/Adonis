<template>
	<div class="main">
		<div class="main_up">
			<div v-if="online == false">
				<el-button-group>
					<el-button @click="registerVisible = true">注册</el-button>
					<el-button @click="loginVisible = true">登录</el-button>
				</el-button-group>
			</div>
			<div v-else>
				欢迎来到聊天室，{{this.name}}
				<el-button-group style="float:right;">
					<el-button type="primary" @click="passwordVisible = true">修改密码</el-button>
					<el-button type="success" @click="nameVisible = true">修改昵称</el-button>
					<el-button type="warning" @click="logOut()">退出登录</el-button>
					<el-button type="danger" @click="omitVisible = true">注销账号</el-button>
				</el-button-group>
			</div>
		</div>

		<el-dialog
			:title="registerTitle"
			:visible.sync="registerVisible"
			width="30%"
			>
			<el-input v-model="id" placeholder="请输入ID"></el-input>
			<el-input v-model="name" placeholder="请输入昵称"></el-input>
			<el-input v-model="password" placeholder="请输入密码"></el-input>
			<el-input v-model="confirm" placeholder="请确认密码"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelRegister()">取 消</el-button>
				<el-button type="primary" @click="register()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="loginTitle"
			:visible.sync="loginVisible"
			width="30%"
			>
			<el-input v-model="id" placeholder="请输入ID"></el-input>
			<el-input v-model="password" placeholder="请输入密码"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelLogin()">取 消</el-button>
				<el-button type="primary" @click="login()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="passwordTitle"
			:visible.sync="passwordVisible"
			width="30%"
			>
			<el-input v-model="password" placeholder="请输入新密码"></el-input>
			<el-input v-model="confirm" placeholder="请确认新密码"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelChangePassword()">取 消</el-button>
				<el-button type="primary" @click="changePassword()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="nameTitle"
			:visible.sync="nameVisible"
			width="30%"
			>
			<el-input v-model="newName" placeholder="请输入新昵称"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelChangeName()">取 消</el-button>
				<el-button type="primary" @click="changeName()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="omitTitle"
			:visible.sync="omitVisible"
			width="30%"
			>
			<span slot="footer" class="dialog-footer">
				<el-button @click="omitVisible = false">取 消</el-button>
				<el-button type="danger" @click="omit()">确 定</el-button>
			</span>
		</el-dialog>
  </div>
</template>

<script>
export default {
	
	name: 'HelloWorld',
	data(){
		return{
			registerTitle: "注册",
			registerVisible: false,
			loginTitle: "登录",
			loginVisible: false,
			passwordTitle: "修改密码",
			passwordVisible: false,
			nameTitle: "修改昵称",
			nameVisible: false,
			omitTitle: "确定要注销当前账号吗？",
			omitVisible: false,
			websock: null,
			reply: [],
			replyCode: "",
			id: "",
			name: "",
			newName: "",
			password: "",
			confirm: "",
			online: false,
		}
	},
	created() {
		this.initWebSocket()
	},
	destroyed: function () {
		this.websocketclose()
	},
	methods: {
		initWebSocket: function () {
			this.websock = new WebSocket("ws://8.130.67.208:8080/ws")
			this.websock.onopen = this.websocketonopen
			this.websock.onerror = this.websocketonerror
			this.websock.onmessage = this.websocketonmessage
			this.websock.onclose = this.websocketclose
		},
		websocketonopen: function () {
			console.log("WebSocket连接成功")
			setInterval(() => {
				this.websock.send("1")
			}, 1000 * 60)
		},
		websocketonerror: function (e) {
			console.log("WebSocket连接发生错误",e)
		},              
		websocketonmessage: function (e) {
			let data = JSON.parse(e.data)
			this.reply.push(data)
		},
		websocketclose: function () {
			console.log("connection closed")
		},
		cancelRegister(){
			this.registerVisible = false
			this.id = ""
			this.name = ""
			this.password = ""
			this.confirm = ""
		},
		register(){
			if (this.password != this.confirm) {
				this.$message.error("两次输入的密码不一致，请重新输入密码！")
				this.password = ""
				this.confirm = ""
			} else {
				let uuid = this.generateUuid()
				let message = JSON.stringify({
					"id": uuid,
					"type": "UserInfoMessage",
					"userInfoMessage": {
						"id": this.id,
						"nickname": this.name,
						"password": this.password,
						"type": "sign_up"}})
				this.websock.send(message)
				this.getReplyCode(uuid)
				setTimeout(()=>{
					switch (this.replyCode) {
						case 201:
							this.$message.error("用户ID已存在，注册失败！")
							this.id = ""
							this.name = ""
							this.password = ""
							this.confirm = ""
							break
						default:
							this.$message.success("注册成功！")
							this.registerVisible = false
							this.id = ""
							this.name = ""
							this.password = ""
							this.confirm = ""
					}
				},1000)
			}
		},
		cancelLogin(){
			this.loginVisible = false
			this.id = ""
			this.password = ""
		},
		login(){
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "UserInfoMessage",
				"userInfoMessage": {
					"id": this.id,
					"password": this.password,
					"type": "sign_in"}})
			this.websock.send(message)
			this.getReplyCode(uuid)
			setTimeout(()=>{
				switch (this.replyCode) {
					case 202:
						this.$message.error("用户ID不存在，登录失败！")
						this.id = ""
						this.password = ""
						break
					case 203:
						this.$message.error("密码错误！")
						this.password = ""
						break
					default:
						this.$message.success("登录成功！")
						this.loginVisible = false
						this.online = true
						this.id = ""
						this.password = ""
				}
			},1000)
		},
		cancelPassword(){
			this.passwordVisible = false
			this.password = ""
			this.confirm = ""
		},
		changePassword(){
			if (this.password != this.confirm) {
				this.$message.error("两次输入的密码不一致，请重新输入密码！")
				this.password = ""
				this.confirm = ""
			} else {
				let uuid = this.generateUuid()
				let message = JSON.stringify({
					"id": uuid,
					"type": "UserInfoMessage",
					"userInfoMessage":{
						"id": this.id,
						"password": this.password,
						"type": "change_password"}})
				this.websock.send(message)
				setTimeout(()=>{
					this.$message.success("密码修改成功！请重新登录")
					this.passwordVisible = false
					this.password = ""
					this.confirm = ""
					this.online = false
				},1000)
			}
		},
		cancelChangeName(){
			this.nameVisible = false
			this.newName = ""
		},
		changeName(){
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "UserInfoMessage",
				"userInfoMessage":{
					"id": this.id,
					"nickname": this.newName,
					"type": "change_nickname"}})
			this.websock.send(message)
			setTimeout(()=>{
				this.$message.success("昵称修改成功！")
				this.nameVisible = false
				this.name = this.newName
				this.newName = ""
			},1000)
		},
		logOut(){
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "UserInfoMessage",
				"userInfoMessage": {
					"id": this.loginId,
					"type":"sign_out"}})
			this.websock.send(message)
			setTimeout(()=>{
				this.id = ""
				this.name = ""
				this.password = ""
				this.online = false
			},1000)
		},
		omit(){
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "UserInfoMessage",
				"userInfoMessage":{ 
					"id": this.loginId,
					"type": "delete"}})
			this.websock.send(message)
			setTimeout(()=>{
				this.omitVisible = false
				this.id = ""
				this.name = ""
				this.password = ""
				this.online = false
			},1000)
		},
		generateUuid(){
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
				var r = Math.random() * 16 | 0,
				v = c == 'x' ? r : (r & 0x3 | 0x8)
				return v.toString(16)
			})
		},
		getReplyCode(uuid){
			setTimeout(()=>{
				for (var i = 0; i < this.reply.length; i++) {
					if (this.reply[i].type == 'ReplyMessage') {
						if (uuid == this.reply[i].replyMessage.messageToReplyId) {
							this.replyCode = this.reply[i].replyMessage.replyCode
							return
						}
					}
				}
			},1000)
		}
	}    
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	.main{
		width: 980px;
		/* border: 1px #1890ff solid; */
		margin-top: 50px;
		height: 790px;
	}
	.main_up{
		width: 980px;
		height: 40px;
		/* border:1px red solid; */
	}
	.main_down{
		width: 981px;
		height: 750px;
		border: 1px #1890ff solid;
		display: flex;
		justify-self: space-between;
	}
	.left{
		width: 300px;
		height: 750px;
		border-right: 1px #1890ff solid;
	}
	.left_up{
		width: 300px;
		height: 450px;
		overflow-y: auto;
		/* border: 1px red solid; */
	}
	.label_title{
		width: 282px;
		height: 25px;
		background-color: #f8f8f8;
		font-weight: 600;
		padding: 8px;
	}
	.left_down{
		width: 300px;
		height: 300px;
		overflow-y: auto;
		/* border: 1px green solid; */
	}
	.right{
		width: 680px;
		height: 750px;
		/* border-right: 1px #1890ff solid; */
	}
	.box{
		width: 250px;
		height: 22px;
		padding: 10px 25px 10px 25px;
		display: flex;
		justify-self: flex-end;
		/* border: 1px red solid; */
	}
	.box:hover{
		background-color: gainsboro;
		cursor: pointer;
	}
	.box_select{
		width: 250px;
		height: 22px;
		padding: 10px 25px 10px 25px;
		display: flex;
		justify-self: flex-end;
		background-color: gainsboro;
	}
	.box_left{
		width: 230px;
		height: 22px;
	}
	.right_left{
		width: 50px;
		height: 22px;
		display: flex;
		justify-content: space-between;
		/* border: 1px red solid; */
	}
	.right_left_count{
		/* border: 1px blue solid; */
		padding-left: 10px;
		width: 20px;
	}
	.right_left_del{
		width: 20px;
		padding-left: 10px;
	}
	.up{
		width: 680px;
		height: 550px;
		overflow-y: scroll;
		overflow-x: hidden;
		/* padding-bottom: 40px; */
		border-bottom: 1px #1890ff solid;
	}
	.msg_left{
		width: 675px;
		/* padding-left: 5px; */
		margin-top: 5px;
		/* border: 1px #1890ff solid; */
	}
	.msg_left_up{
		height: 25px;
		margin-top: 5px;
	}
	.msg_left_down{
		height: 25px;
		/* border: 1px #1890ff solid; */
		padding-left: 10px;
	}
	.msg_right{
		width: 660px;
		/* padding-right: 20px; */
		margin-top: 5px;
		/* border: 1px #1890ff solid; */
		text-align: right;
	}
	.msg_right_up{
		height: 25px;
		
	}
	.msg_right_down{
		height: 25px;
		/* border: 1px #1890ff solid; */
		padding-right: 10px;
	}
	.down{
		width: 680px;
		height: 200px;
		/* border: 1px red solid; */
	}
</style>
