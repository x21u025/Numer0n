const _sleep = async (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const template = {"user":{"id":"","name":""},"message":"","type":""};
var ws;
let userId;
let chatCount = 0;
let queueCount = 0;
function connect() {
	ws = new WebSocket(url);

	ws.onmessage = function(receive) {
		let json = JSON.parse(receive.data);
		if(json.type == "USER") {
			userId = json.user.id;
		} else if(json.type == "CHAT_COUNT") {
			chatCount = json.message;
			changeTitle();
		} else if(json.type == "QUEUE_COUNT") {
			queueCount = json.message;
			changeTitle();
		} else if(json.type == "CHAT") {
			if(json.user.id == userId) {
				addMyChat(json.message);
			} else {
				addYourChat(json.user.name + "\n" + json.message);
			}
			chatBox.scrollTop = chatBox.scrollHeight - chatBox.clientHeight;
		}
	};

	ws.onopen = function() {
		console.log("Connect");
	};

	ws.onerror = function (e) {
		console.log(e);
	};

	ws.onclose = function (e) {
		console.log(e);
		connect();
	};
}
$(function() {
	connect();
});

function changeTitle() {
	$("#bms_chat_user_name").text("全体チャット(" + chatCount + ")(" + queueCount + ")");
}


let chatRoot;
let chatBox;
let chatMsg;
window.addEventListener('load', () => {
	chatRoot = document.getElementById("msgbox");
	chatBox = document.getElementById("bms_messages");
	chatMsg = document.getElementById("bms_send_message");
});
function toggleChat() {
	chatRoot.classList.toggle("close");
}
function addYourChat(message) {
	addChat(message, "left");
}
function addMyChat(message) {
	addChat(message, "right");
}
function addChat(message, lr) {
	let root = document.createElement("div");
	let box = document.createElement("div");
	let content = document.createElement("div");
	let text = document.createElement("div");
	let clear = document.createElement("div");

	root.classList.add("bms_message");
	root.classList.add("bms_" + lr);
	box.classList.add("bms_message_box");
	content.classList.add("bms_message_content");
	text.classList.add("bms_message_text");
	text.style = "white-space: pre-wrap; word-wrap: break-word;";
	text.textContent = message;
	clear.classList.add("bms_clear");

	chatBox.appendChild(root);
	root.appendChild(box);
	box.appendChild(content);
	content.appendChild(text);
	chatBox.appendChild(clear);
}
function sendChat() {
	if(chatMsg.value.trim() != "") {
		let json = JSON.parse(JSON.stringify(template));
		json.user.id = userId;
		json.message = chatMsg.value;
		ws.send(JSON.stringify(json));
		chatMsg.value = "";
	}
}

/** New Line Code */
const LF = "\n";
/**
 * New Line with Alt-Enter
 * @param {Object} _event event
 * @param {Object} _this element
 */
function onKeyNewLine(_event, _this) {
	if(_event.key === "Enter") {
		if(_event.shiftKey) {
				// TODO: サブストリングのブランクを削除すること（２か所）
				let first = _this.value.substring(0, _this.selectionStart);
				let second = _this.value.substring(_this.selectionEnd);
				_this.value = first + LF + second;
				_this.selectionStart = first.length + 1;
				_this.selectionEnd = _this.selectionStart;
		} else {
			sendChat();
		}
		_event.preventDefault();
		return false;
	 }
};