const _sleep = async (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const template = {"user":{"id":"","name":""},"hab":{"hit":0,"blow":0},"number":"","message":""};
var ws;
let wsFirst = true;
let n;
let surrender = false;
let fin = false;
function connect() {
	ws = new WebSocket(url);

	ws.onmessage = function(receive) {
		$("#message").html($("#message").html() + "<br>" + receive.data);

		let json = JSON.parse(receive.data);

		if(json.message == "setNumber") {
			if(json.user.id == userId) {
				changeTitle("Wait");
				myNum = json.number;
				typeNum = "	 ";
				if(!isDl) {
					showMyNum();
				} else {
					showTypeNum();
				}
			} else {
				showYourNum("⋆⋆⋆");
				toggleYour();
			}
		} else if(json.message == "startNumer0n") {
			if(userId == json.number.split("|")[0]) {
				n = 1;
				toggleMy();
				document.getElementById("correct").disabled = false;
				changeTitle("Your Turn");
			} else {
				n = 2;
				toggleYour();
				changeTitle("Wait Turn");
			}
		} else if(json.message == "hitNumber") {
			showYourNum(json.number);
			document.getElementById("correct").disabled = true;
			document.getElementById("correct").style = "display:none";
			document.getElementById("fin").style = "display:block";
			document.getElementById("surrender").disabled = true;


			document.getElementById("my").children[0].classList.value = "cards";
			document.getElementById("your").children[0].classList.value = "cards";

			if(json.hab.hit == n) {
				document.getElementById("my").children[0].classList.toggle("can");
				document.getElementById("your").children[0].classList.toggle("discan");
				changeTitle("You Win");
			} else {
				if(json.hab.hit == 3) {
					document.getElementById("my").children[0].classList.toggle("draw");
					document.getElementById("your").children[0].classList.toggle("draw");
					changeTitle("Draw");
				} else {
					document.getElementById("your").children[0].classList.toggle("can");
					document.getElementById("my").children[0].classList.toggle("discan");
					changeTitle("You Lose");
				}
			}

			if(isDl) {
				dl();
			}

			fin = true;
		} else if(json.message == "log") {
			let newCell;
			if(json.user.id == userId) {
				newCell = document.getElementById("mylog").children[0].insertRow().insertCell();
				toggleYour();
				changeTitle("Wait Turn");
			} else {
				newCell = document.getElementById("yourlog").children[0].insertRow().insertCell();
				document.getElementById("correct").disabled = false;
				toggleAll();
				changeTitle("Your Turn");
			}

			let div = document.createElement("div");
			let spanNum = document.createElement("span");
			let spanHab = document.createElement("span");
			let spanHit = document.createElement("span");
			let spanBlow = document.createElement("span");
			spanHab.appendChild(spanHit);
			spanHab.appendChild(spanBlow);
			div.appendChild(spanNum);
			div.appendChild(spanHab);

			spanNum.classList.add("lognum");
			spanNum.innerText = json.number;
			spanHab.classList.add("loghab");
			spanHit.innerText = json.hab.hit;
			spanHit.classList.add("hit");
			spanBlow.innerText = json.hab.blow;
			spanBlow.classList.add("blow");

			newCell.appendChild(div);
		} else if(json.message == "Surrender") {
			surrender = true;
			if(json.user.id == userId) {
				//自分が降参
				document.getElementById("lose").style = "";
			} else {
				//相手が降参
				document.getElementById("win").style = "";
			}
		} else if(json.message == "Message") {
			if(json.user.id == userId) {
				addMyChat(json.number);
			} else {
				addYourChat(json.number);
			}
		} else if(json.message == "Player") {
			if(json.user.id != userId) {
				document.getElementById("bms_chat_user_name").textContent = json.user.name;
			}
		} else if(json.message == "Close") {
			window.location.href = "Mypage";
		} else if(json.message == "ChatClose") {
			chatMsg.value = "対戦相手が切断したため、使用できません";
			document.getElementById("bms_send_btn").onclick = null;
		}
	};

	ws.onopen = function() {
		console.log("Connect");
		if(wsFirst) {
			changeTitle("Set Number");
			wsFirst = false;
		}
	};

	ws.onerror = function (e) {
		console.log(e);

//		var XHR = new XMLHttpRequest();
//		XHR.open("POST", "api/websocket/log", true);
//		let data = {};
//		data['message'] = e;
//		console.log(EncodeHTMLForm(data));
//		XHR.setRequestHeader( 'Content-Type', 'application/x-www-form-urlencoded' );
//		XHR.send(EncodeHTMLForm(data));
	};

	ws.onclose = function (e) {
		console.log(e);
		connect();
	};
};
$(function() {
	connect();
});

function send() {
	ws.send($("#text").val());
}

let isDl = true;
let myNum = undefined;
function dl() {
	let dl = document.getElementById("dl");
	dl.classList.toggle("dl");
	isDl = dl.className != "dl";
	if(isDl) {
		// 入力している文字を表示
		showTypeNum();
	} else {
		if(myNum != undefined) {
			// 自分の数字を表示
			showMyNum();
		}
	}
}


let typeNum = "	 ";
function setNum(num) {
	if(typeNum.substr(1, 1) != num && typeNum.substr(2, 1) != num) {
		typeNum += num;
		if(typeNum.length == 4) {
			typeNum = typeNum.substr(1, 3);
		}
	}
	if(isDl) {
		showTypeNum();
	}
}

function showTypeNum() {
	for(i = 0; i < 3; i++) {
		document.getElementById("my" + (i + 1)).innerText = typeNum.substr(i, 1);
	}
}
function showMyNum() {
	for(i = 0; i < 3; i++) {
		document.getElementById("my" + (i + 1)).innerText = myNum.substr(i, 1);
	}
}
function showYourNum(yourNum) {
	for(i = 0; i < 3; i++) {
		document.getElementById("your" + (i + 1)).innerText = yourNum.substr(i, 1);
	}
}
function toggleMy() {
	toggle("my");
}
function toggleYour() {
	toggle("your");
}
function toggleAll() {
	toggleMy();
	toggleYour();
}
function toggle(who) {
	document.getElementById(who).children[0].classList.toggle("can");
	document.getElementById(who).children[0].classList.toggle("discan");
}



function correct() {
	if(/[0-9]{3}/.test(typeNum)) {
		let json = JSON.parse(JSON.stringify(template));
		json.number = typeNum;
		ws.send(JSON.stringify(json));
		document.getElementById("correct").disabled = true;
		toggleMy();
	}
}

function finish() {
	let json = JSON.parse(JSON.stringify(template));
	json.message = "Surrender";
	ws.send(JSON.stringify(json));
}
async function next() {
	await _sleep(1000);
	location.href = "Mypage";
}

function changeTitle(title) {
	$("#title").text(title);
}

window.addEventListener('beforeunload', function (e) {
	if(!(surrender || fin)) {
		sessionStorage.setItem("reloading", "true");
		e.preventDefault();
		// メッセージを表示する
		e.returnValue = '本当にページ移動しますか？';
	}

});


let chatRoot;
let chatBox;
let chatMsg;
window.onload = () => {
	chatRoot = document.getElementById("msgbox");
	chatBox = document.getElementById("bms_messages");
	chatMsg = document.getElementById("bms_send_message");

	var reloading = sessionStorage.getItem("reloading");
	if (!!reloading) {
		sessionStorage.removeItem("reloading");
		//window.location.href = "Mypage";
	}
}
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
	text.textContent = message;
	clear.classList.add("bms_clear");

	chatBox.appendChild(root);
	root.appendChild(box);
	box.appendChild(content);
	content.appendChild(text);
	chatBox.appendChild(clear);
}
function sendChat() {
	let json = JSON.parse(JSON.stringify(template));
	json.message = "Message";
	json.number = chatMsg.value;
	ws.send(JSON.stringify(json));
	chatMsg.value = "";
}

function EncodeHTMLForm(data) {
	var params = [];
	for(var name in data) {
		var value = data[ name ];
		var param = encodeURIComponent(name) + '=' + encodeURIComponent(value);
		params.push(param);
	}
	return params.join('&').replace(/%20/g, '+');
}