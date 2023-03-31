<template>
	<div class="main">	
		<div class="main_up">
			<el-button icon="el-icon-edit" size="mini" @click="registerDialog()">注册</el-button>
		</div>

		<el-dialog
			:title="registerTitle"
			:visible.sync="registerVisible"
			width="30%"
			>
			<span slot="footer" class="dialog-footer">
				<el-button @click="registerVisible = false">取 消</el-button>
				<el-button type="primary" @click="register">确 定</el-button>
			</span>
		</el-dialog>
  </div>
</template>

<script>
export default {
	
	name: 'HelloWorld',
	data(){
		return{
			registerTitle: "",
			registerVisible: false,
			websock: null,
		}
	},
	created() {          
		this.initWebSocket();
	},   
	destroyed: function () {           
		this.websocketclose();        
	},
	methods: {            
		initWebSocket: function () {
			this.websock = new WebSocket("ws://8.130.67.208:8080/ws");                
			this.websock.onopen = this.websocketonopen;                
			this.websock.onerror = this.websocketonerror;                
			this.websock.onmessage = this.websocketonmessage;                
			this.websock.onclose = this.websocketclose;
		},              
		websocketonopen: function () {
			console.log("WebSocket连接成功");
			let submitCode = JSON.stringify({
				"id": "e6279dbd-a9a1-4207-b9da-0e411193f947",
				"type": "UserInfoMessage",
				"userInfoMessage": {
					"id": "8569",
					"nickname": "乌有之乡",
					"password": "56897z",
					"type": "sign_up"
				}
			})
			this.websock.send(submitCode);
		},              
		websocketonerror: function (e) { 
			console.log("WebSocket连接发生错误",e);              
		},              
		websocketonmessage: function (e) {
			console.log(e.data);
			this.websock.close();
		},              
		websocketclose: function (e) {
			console.log("connection closed",e);       
		},
		registerDialog(){
			this.registerTitle = '注册';
			this.registerVisible = true;
		},
		register(){
		},
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
