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
				欢迎来到聊天室 {{this.name}}<br/><br/>
				<el-button-group style="float:left;">
					<el-button type="primary" @click="friendRequestVisible = true">添加好友</el-button>
					<el-button type="primary" @click="friendExistVisible = true">查询用户是否存在</el-button>
					<el-button type="primary" @click="friendOnlineVisible = true">查询用户是否在线</el-button>
					<el-button type="primary" @click="friendRelationVisible = true">查询关系</el-button>
				</el-button-group>
				<el-button-group style="float:right;">
					<el-button type="primary" @click="passwordVisible = true">修改密码</el-button>
					<el-button type="success" @click="nameVisible = true">修改昵称</el-button>
					<el-button type="warning" @click="logOut()">退出登录</el-button>
					<el-button type="danger" @click="omitVisible = true">注销账号</el-button>
				</el-button-group>
			</div>
		</div>
		
		<div v-if="online == true">
			<div class="main_down">
				<div class="left">
					<div class="left_up">
						<div class="label_title">
							好友请求
						</div>
						<div v-for="item in friendReceiveList" :key="item.id" class="box_left">
							ID: {{item.friendInfoMessage.id}}<br/>
							<el-button type="success" @click="acceptFriend(item.friendInfoMessage.id)">接受</el-button>
							<el-button type="danger" @click="rejectFriend(item.friendInfoMessage.id)">拒绝</el-button>
						</div>
						<div v-for="item in friendSendingList" :key="item.id" class="box">
							ID: {{item.id}}<br/>
							状态：{{item.status}}
						</div>
					</div>
					<div class="left_down">
						<div class="label_title">
							好友列表
						</div>
						<div v-for="item in friendList" :key="item.id" class="box_left">
							ID: {{item.id}}<br/>
							状态: {{item.status}}<br/>
							<el-button-group>
								<el-button type="success" @click="chatId = item.id">聊天</el-button>
								<el-button type="warning" @click="blacklistFriend(item.id)">拉黑</el-button>
								<el-button type="danger" @click="deleteFriend(item.id)">删除</el-button>
							</el-button-group>
						</div>
					</div>
				</div>
				
				<div class="right">
					<div class="label">
						{{this.chatId}}
					</div>
					<div class="up" ref="element">
						<div v-for="item in chatList" :key="item.uuid" class="msg_left">
							<div v-if="item.senderId == chatId || item.receiverId == chatId">
								<div class="msg_right_up">
									{{item.senderId}}&nbsp;{{item.time}}
								</div>
								<div class="msg_right_down">
									{{item.content}}
								</div>
							</div>
						</div>
					</div>
					<div class="down">
						<el-input
						type="textarea"
						:rows="7"
						placeholder="请输入内容，回车发送！"
						@keyup.enter.native = "sendMsg"
						v-model="textarea">
						</el-input>
					</div>
				</div>
				
			</div>
		</div>
		
		<el-dialog
			:title="registerTitle"
			:visible.sync="registerVisible"
			width="30%"
			>
			<el-input v-model="id" placeholder="请输入ID"></el-input>
			<el-input v-model="name" placeholder="请输入昵称"></el-input>
			<el-input v-model="password" type="password" placeholder="请输入密码"></el-input>
			<el-input v-model="confirm" type="password" placeholder="请确认密码"></el-input>
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
			<el-input v-model="password" type="password" placeholder="请输入密码"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelLogin()">取 消</el-button>
				<el-button type="primary" @click="login()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="friendRequestTitle"
			:visible.sync="friendRequestVisible"
			width="30%"
			>
			<el-input v-model="requestId" placeholder="请输入好友ID"></el-input>
			<el-input v-model="notes" placeholder="请输入好友备注名"></el-input>
			<el-input v-model="memo" placeholder="请输入申请备注"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelFriendRequest()">取 消</el-button>
				<el-button type="primary" @click="friendRequest()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="friendExistTitle"
			:visible.sync="friendExistVisible"
			width="30%"
			>
			<el-input v-model="existId" placeholder="请输入用户ID"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelFriendExist()">取 消</el-button>
				<el-button type="primary" @click="friendExist()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="friendOnlineTitle"
			:visible.sync="friendOnlineVisible"
			width="30%"
			>
			<el-input v-model="onlineId" placeholder="请输入用户ID"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelFriendOnline()">取 消</el-button>
				<el-button type="primary" @click="friendOnline()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="friendRelationTitle"
			:visible.sync="friendRelationVisible"
			width="30%"
			>
			<el-input v-model="relationId" placeholder="请输入ID"></el-input>
			<span slot="footer" class="dialog-footer">
				<el-button @click="cancelFriendRelation()">取 消</el-button>
				<el-button type="primary" @click="friendRelation()">确 定</el-button>
			</span>
		</el-dialog>
		
		<el-dialog
			:title="passwordTitle"
			:visible.sync="passwordVisible"
			width="30%"
			>
			<el-input v-model="password" type="password" placeholder="请输入新密码"></el-input>
			<el-input v-model="confirm" type="password" placeholder="请确认新密码"></el-input>
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
			friendRequestTitle: "添加好友",
			friendRequestVisible: false,
			friendExistTitle: "查询用户是否存在",
			friendExistVisible: false,
			friendOnlineTitle: "查询用户是否在线",
			friendOnlineVisible: false,
			friendRelationTitle: "查询用户关系",
			friendRelationVisible: false,
			passwordTitle: "修改密码",
			passwordVisible: false,
			nameTitle: "修改昵称",
			nameVisible: false,
			omitTitle: "确定要注销当前账号吗？",
			omitVisible: false,
			websock: null,
			reply: [],
			userCode: "",
			friendCode: "",
			id: "",
			name: "",
			newName: "",
			password: "",
			confirm: "",
			online: false,
			requestId: "",
			notes: "",
			memo: "",
			existId: "",
			onlineId: "",
			relationId: "",
			friendSendingList: [],
			friendReceiveList: [],
			friendList: [],
			chatId: "",
			textarea: "",
			chatList: []
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
		},
		websocketonerror: function (e) {
			console.log("WebSocket连接发生错误",e)
		},              
		websocketonmessage: function (e) {
			let data = JSON.parse(e.data)
			console.log(data)
			this.reply.push(data)
			if (this.reply.length == 1) {
				if (this.reply[0].type == "friendInfoMessage" && this.reply[0].friendInfoMessage.code != 403) {
					this.friendPassive()
				} else if (this.reply[0].type == "replyMessage") {
					this.chatTime()
				} else if (this.reply[0].type == "dialogueMessage") {
					this.chatOperation(this.reply[0])
				}
			} else if (this.reply.length == 2) {
				if (this.reply[0].type == "replyMessage") {
					if (this.reply[1].type == "userInfoMessage") {
						this.userOperation()
					} else if (this.reply[1].type == "friendInfoMessage") {
						this.friendActive()
					}
				} else if (this.reply[0].type == "friendInfoMessage") {
					if (this.reply[1].type == "dialogueMessage") {
						this.friendPassive()
					}
				}
			} else if (this.reply.length == 3) {
				if (this.reply[0].type == "replyMessage") {
					if (this.reply[1].type == "dialogueMessage") {
						if (this.reply[2].type == "friendInfoMessage") {
							this.chatOperation(this.reply[1])
						}
					}
				}
			}
		},
		websocketclose: function () {
			console.log("connection closed")
			this.initWebSocket()
		},
		cancelRegister() {
			this.registerVisible = false
			this.id = ""
			this.name = ""
			this.password = ""
			this.confirm = ""
		},
		register() {
			if (this.password != this.confirm) {
				this.$message.error("两次输入的密码不一致，请重新输入密码！")
				this.password = ""
				this.confirm = ""
			} else {
				let uuid = this.generateUuid()
				let message = JSON.stringify({
					"id": uuid,
					"type": "userOpMessage",
					"userOpMessage": {
						"code": 102,
						"id": this.id,
						"nickname": this.name,
						"password": this.password}})
				this.websock.send(message)
				setTimeout(()=>{
					switch (this.userCode) {
						case 201:
							this.$message.error("用户ID已存在，无法注册！")
							this.id = ""
							this.name = ""
							this.password = ""
							this.confirm = ""
							break
						case 206:
							this.$message.error("注册时未传入完整信息！")
							this.id = ""
							this.name = ""
							this.password = ""
							this.confirm = ""
							break
						case 200:
							this.$message.success("注册成功！")
							this.registerVisible = false
							this.id = ""
							this.password = ""
							this.confirm = ""
							break
					}
				},1000)
			}
		},
		cancelLogin() {
			this.loginVisible = false
			this.id = ""
			this.password = ""
		},
		login() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "userOpMessage",
				"userOpMessage": {
					"code": 100,
					"id": this.id,
					"nickname": this.name,
					"password": this.password}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.userCode) {
					case 202:
						this.$message.error("用户ID不存在，登录失败！")
						this.id = ""
						this.password = ""
						break
					case 203:
						this.$message.error("密码错误！")
						this.password = ""
						break
					case 200:
						this.$message.success("登录成功！")
						this.password = ""
						this.loginVisible = false
						this.online = true
						break
				}
			},1000)
		},
		cancelFriendRequest() {
			this.friendRequestVisible = false
			this.requestId = ""
		},
		friendRequest() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 300,
					"subjectId": this.id,
					"objectId": this.requestId,
					"customNickname": this.notes,
					"memo": this.memo}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.friendCode) {
					case 400:
						this.$message.success("好友申请已发送！")
						this.friendRequestVisible = false
						for (var i = 0;i < this.friendSendingList.length;i++) {
							if (this.requestId == this.friendSendingList[i]["id"]) {
								this.friendSendingList[i]["status"] = "好友申请已发送"
								break
							}
						}
						if (i == this.friendSendingList.length) {
							this.friendSendingList.push({"id": this.requestId, "status": "好友申请已发送"})
						}
						this.requestId = ""
						this.notes = ""
						this.memo = ""
						break
				}
			},1000)
		},
		cancelFriendExist() {
			this.friendExistVisible = false
			this.existId = ""
		},
		friendExist() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 304,
					"subjectId": this.id,
					"objectId": this.existId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.friendCode) {
					case 406:
						this.$message.success("此用户存在")
						this.friendExistVisible = false
						this.existId = ""
						break
					case 407:
						this.$message.error("此用户不存在，已注销或从未注册过！")
						this.friendExistVisible = false
						this.existId = ""
						break
				}
			},1000)
		},
		cancelFriendOnline() {
			this.friendOnlineVisible = false
			this.onlineId = ""
		},
		friendOnline() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 305,
					"subjectId": this.id,
					"objectId": this.onlineId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.friendCode) {
					case 408:
						this.$message.success("此用户在线")
						this.friendOnlineVisible = false
						this.onlineId = ""
						break
					case 409:
						this.$message.error("此用户不在线！")
						this.friendOnlineVisible = false
						this.onlineId = ""
						break
				}
			},1000)
		},
		cancelFriendRelation() {
			this.friendRelationVisible = false
			this.relationId = ""
		},
		friendRelation() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 308,
					"subjectId": this.id,
					"objectId": this.relationId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.friendCode) {
					case 410:
						this.$message("此用户是您的单向好友（您列表有ta，ta列表没您）")
						this.friendRelationVisible = false
						this.relationId = ""
						break
					case 411:
						this.$message("此用户是您的单向好友（您列表没ta，ta列表有您）")
						this.friendRelationVisible = false
						this.relationId = ""
						break
					case 412:
						this.$message("此用户和您是双向好友")
						this.friendRelationVisible = false
						this.relationId = ""
						break
					case 413:
						this.$message("此用户和您没有任何好友关系")
						this.friendRelationVisible = false
						this.relationId = ""
						break
				}
			},1000)
		},
		cancelChangePassword() {
			this.passwordVisible = false
			this.password = ""
			this.confirm = ""
		},
		changePassword() {
			if (this.password != this.confirm) {
				this.$message.error("两次输入的密码不一致，请重新输入密码！")
				this.password = ""
				this.confirm = ""
			} else {
				let uuid = this.generateUuid()
				let message = JSON.stringify({
					"id": uuid,
					"type": "userOpMessage",
					"userOpMessage": {
						"code": 105,
						"id": this.id,
						"nickname": this.name,
						"password": this.password}})
				this.websock.send(message)
				setTimeout(()=>{
					switch (this.userCode) {
						case 205:
							this.$message.error("消息中未传入新密码，无法更新！")
							break
						case 200:
							this.$message.success("密码修改成功！请重新登录")
							this.passwordVisible = false
							this.online = false
							this.password = ""
							this.confirm = ""
							break
					}
				},1000)
			}
		},
		cancelChangeName() {
			this.nameVisible = false
			this.newName = ""
		},
		changeName() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "userOpMessage",
				"userOpMessage": {
					"code": 104,
					"id": this.id,
					"nickname": this.name,
					"password": this.password}})
			this.websock.send(message)
			setTimeout(()=>{
				switch (this.userCode) {
					case 204:
						this.$message.error("消息中未传入新昵称，无法更新！")
						break
					case 200:
						this.$message.success("昵称修改成功！")
						this.nameVisible = false
						this.name = this.newName
						this.newName = ""
						break
				}
			},1000)
		},
		logOut() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "userOpMessage",
				"userOpMessage": {
					"code": 101,
					"id": this.id,
					"nickname": this.name,
					"password": this.password}})
			this.websock.send(message)
			setTimeout(()=>{
				this.id = ""
				this.name = ""
				this.password = ""
				this.online = false
				this.reply = []
			},1000)
		},
		omit() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "userOpMessage",
				"userOpMessage": {
					"code": 103,
					"id": this.id,
					"nickname": this.name,
					"password": this.password}})
			this.websock.send(message)
			setTimeout(()=>{
				this.omitVisible = false
				this.id = ""
				this.name = ""
				this.password = ""
				this.online = false
				this.reply = []
			},1000)
		},
		acceptFriend(objectId) {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 301,
					"subjectId": this.id,
					"objectId": objectId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				for (var i = 0;i < this.friendReceiveList.length;i++) {
					if (objectId == this.friendReceiveList[i].friendInfoMessage.id) {
						this.friendReceiveList.splice(i, 1)
					}
				}
				this.friendList.push({"id": objectId, status: "正常好友"})
			},1000)
		},
		rejectFriend(objectId) {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 302,
					"subjectId": this.id,
					"objectId": objectId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				for (var i = 0;i < this.friendReceiveList.length;i++) {
					if (objectId == this.friendReceiveList[i].friendInfoMessage.id) {
						this.friendReceiveList.splice(i, 1)
					}
				}
			},1000)
		},
		blacklistFriend(objectId) {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 306,
					"subjectId": this.id,
					"objectId": objectId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				for (var i = 0;i < this.friendList.length;i++) {
					if (objectId == this.friendList[i].id) {
						this.friendList[i].status = "已拉黑"
					}
				}
			},1000)
		},
		deleteFriend(objectId) {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "friendOpMessage",
				"friendOpMessage": {
					"code": 303,
					"subjectId": this.id,
					"objectId": objectId,
					"customNickname": "",
					"memo": ""}})
			this.websock.send(message)
			setTimeout(()=>{
				for (var i = 0;i < this.friendList.length;i++) {
					if (objectId == this.friendList[i].id) {
						this.friendList.splice(i, 1)
					}
				}
			},1000)
		},
		sendMsg() {
			let uuid = this.generateUuid()
			let message = JSON.stringify({
				"id": uuid,
				"type": "dialogueMessage",
				"dialogueMessage": {
					"senderId": this.id,
					"receiverId": this.chatId,
					"content": this.textarea,
					"occurredTime": 0,
					"lastedTime": 5000}})
			this.websock.send(message)
			this.chatList.push({
				uuid: uuid,
				senderId: this.id,
				receiverId: this.chatId,
				time: 0,
				content: this.textarea})
			this.textarea = ""
		},
		generateUuid() {
			return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
				var r = Math.random() * 16 | 0,
				v = c == 'x' ? r : (r & 0x3 | 0x8)
				return v.toString(16)
			})
		},
		userOperation() {
			if (this.reply[0].replyMessage.replyCode == 0) {
				this.userCode = this.reply[1].userInfoMessage.code
			} else {
				this.exceptionCode(this.reply[0].replyMessage.replyCode)
			}
			this.reply = []
		},
		exceptionCode(code) {
			switch (code) {
				case 100:
					this.$message.error("不存在的消息类型！")
					break
				case 101:
					this.$message.error("消息类型与消息内容不符！")
					break
				case 102:
					this.$message.error("不存在的子消息MessageCode！")
					break
				case 200:
					this.$message.error("不正确的用户操作消息MessageCode！")
					break
				case 201:
					this.$message.error("消息内用户ID与session拥有用户ID不一致，可能有破坏者伪装成他人发送消息！")
					break
				case 300:
					this.$message.error("不正确的好友操作消息MessageCode！")
					break
				case 301:
					this.$message.error("发出好友操作的用户自己已经注销，这是一种极端错误！")
					break
				case 302:
					this.$message.error("好友操作涉及双方不能相同！")
					break
				case 401:
					this.$message.error("消息发送与接收有一方不存在，可能已注销！")
					break
				case 402:
					this.$message.error("消息接收方和自己不是双向好友就发送了消息！")
					break
			}
		},
		friendActive() {
			if (this.reply[0].replyMessage.replyCode == 0) {
				this.friendCode = this.reply[1].friendInfoMessage.code
			} else {
				this.exceptionCode(this.reply[0].replyMessage.replyCode)
			}
			this.reply = []
		},
		friendPassive() {
			switch (this.reply[0].friendInfoMessage.code) {
				case 401:
					this.friendReceiveList.push(this.reply[0])
					break
				case 403:
					this.$message.success("对方同意了你的好友申请")
					for (var i = 0;i < this.friendSendingList.length;i++) {
						if (this.reply[0].friendInfoMessage.id == this.friendSendingList[i]["id"]) {
							this.friendSendingList[i]["status"] = "好友申请已同意"
							this.friendList.push({"id": this.reply[0].friendInfoMessage.id, status: "正常好友"})
							break
						}
					}
					this.chatList.push({
						uuid: this.reply[1].id,
						senderId: this.reply[1].dialogueMessage.senderId,
						receiverId: this.reply[1].dialogueMessage.receiverId,
						time: this.changeTime(this.reply[1].dialogueMessage.occurredTime),
						content: this.reply[1].dialogueMessage.content})
					break
				case 405:
					this.$message.warning("对方拒绝了你的好友申请！")
					for (i = 0;i < this.friendSendingList.length;i++) {
						if (this.reply[0].friendInfoMessage.id == this.friendSendingList[i]["id"]) {
							this.friendSendingList[i]["status"] = "好友申请被拒绝"
							break
						}
					}
					break
			}
			this.reply = []
		},
		chatOperation(message) {
			this.chatList.push({
				uuid: message.id,
				senderId: message.dialogueMessage.senderId,
				receiverId: message.dialogueMessage.receiverId,
				time: this.changeTime(message.dialogueMessage.occurredTime),
				content: message.dialogueMessage.content})
			this.reply = []
		},
		chatTime() {
			for (var i = 0;i < this.chatList.length;i++) {
				if (this.reply[0].replyMessage.messageToReplyId == this.chatList[i]["uuid"]) {
					this.chatList[i]["time"] = this.changeTime(this.reply[0].replyMessage.occurredTime)
					this.reply = []
					return
				}
			}
		},
		changeTime(time) {
			var date = new Date(time)
			var month = date.getMonth() + 1
			var days = date.getDate()
			var hours = date.getHours()
			var minutes = date.getMinutes()
			var second = date.getSeconds()
			if (month < 10) {
				month = '0' + month
			}
			if (days < 10) {
				days = '0' + days
			}
			if (hours < 10) {
				hours = "0" + hours
			}
			if (minutes < 10) {
				minutes = "0" + minutes
			}
			if (second < 10) {
				second = "0" + second
			}
			var result = date.getFullYear() + "-" + month + "-" + days + " " + hours + ":" + minutes + ":" + second
			return result
		}
	}
}
</script>

<style scoped>
	.main{
		width: 1080px;
		margin-top: 50px;
		height: 790px;
	}
	.main_up{
		width: 1080px;
		height: 40px;
	}
	.main_down{
		width: 1081px;
		height: 750px;
		border: 1px #1890ff solid;
		display: flex;
		justify-self: space-between;
	}
	.left{
		width: 400px;
		height: 750px;
		border-right: 1px #1890ff solid;
	}
	.left_up{
		width: 400px;
		height: 450px;
		overflow-y: auto;
	}
	.label_title{
		width: 380px;
		height: 25px;
		background-color: #DCE2f1;
		font-weight: 600;
		padding: 8px;
	}
	.label{
		width: 644px;
		height: 25px;
		background-color: #DCE2f1;
		font-weight: 600;
		padding: 8px;
	}
	.left_down{
		width: 400px;
		height: 300px;
		overflow-y: auto;
	}
	.right{
		width: 700px;
		height: 750px;
	}
	.box{
		width: 250px;
		height: 40px;
		padding: 10px 25px 10px 25px;
		display: flex;
		justify-self: flex-end;
	}
	.box:hover{
		background-color: gainsboro;
		cursor: pointer;
	}
	.box_left{
		width: 350px;
		height: 90px;
		padding: 10px 25px 10px 25px;
	}
	.up{
		width: 680px;
		height: 550px;
		overflow-y: scroll;
		overflow-x: hidden;
		border-bottom: 1px #1890ff solid;
	}
	.msg_left{
		width: 675px;
		margin-top: 5px;
	}
	.msg_right_up{
		height: 25px;
		
	}
	.msg_right_down{
		height: 25px;
		padding-right: 10px;
	}
	.down{
		width: 680px;
		height: 200px;
	}
</style>
