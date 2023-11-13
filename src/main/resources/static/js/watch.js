const _sleep = async (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const template = {"user":{"id":"","name":""},"hab":{"hit":0,"blow":0},"number":"","message":""};
var ws;
let set = 0;

//
let n;
let surrender = false;
let fin = false;
//

let players = [{"id":"","name":"","num":""},{"id":"","name":"","num":""}];
$(function() {
	ws = new WebSocket(url);

	ws.onmessage = function(receive) {
		$("#message").html($("#message").html() + "<br>" + receive.data);

		let jsons = JSON.parse(receive.data);
		for(let json of jsons) {
			switch(json.message) {
				case "CREATE":
					break;
				case "PLAYER":
					players[json.number - 1].id = json.user.id;
					players[json.number - 1].name = json.user.name;
					break;
				case "SET_NUMBER":
					if(json.user.id == players[0].id) {
						players[0].num = json.number;
						changeTitle("Player1 is OK");
						showMyNum("⋆⋆⋆");
						toggleMy();
						set += 1;
					} else {
						players[1].num = json.number;
						changeTitle("Player2 is OK");
						showYourNum("⋆⋆⋆");
						toggleYour();
						set += 2;
					}
					if(set >= 3) {
						changeTitle("Player1 Turn");
						toggleMy();
					}
					break;
				case "ANSWER_NUMBER":
					let newCell;
					toggleAll();
					if(json.user.id == players[0].id) {
						newCell = document.getElementById("mylog").children[0].insertRow().insertCell();
						changeTitle("Player2 Turn");
						showMyNum(json.number);
					} else {
						newCell = document.getElementById("yourlog").children[0].insertRow().insertCell();
						document.getElementById("correct").disabled = false;
						changeTitle("Player1 Turn");
						showYourNum(json.number);
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
					break;
				case "WIN_PLAYER_1":
					winReset();
					document.getElementById("my").children[0].classList.toggle("can");
					document.getElementById("your").children[0].classList.toggle("discan");
					changeTitle("Player1 Win");
					break;
				case "WIN_PLAYER_2":
					winReset();
					document.getElementById("your").children[0].classList.toggle("can");
					document.getElementById("my").children[0].classList.toggle("discan");
					changeTitle("Player2 Win");
					break;
				case "DRAW":
					winReset();
					document.getElementById("my").children[0].classList.toggle("draw");
					document.getElementById("your").children[0].classList.toggle("draw");
					changeTitle("Draw");
					break;
				case "Surrender":
					winReset();
					if(json.user.id == players[0].id) {
						document.getElementById("your").children[0].classList.toggle("can");
						document.getElementById("my").children[0].classList.toggle("discan");
						changeTitle("Player1 Surrender");
					} else {
						document.getElementById("my").children[0].classList.toggle("can");
						document.getElementById("your").children[0].classList.toggle("discan");
						changeTitle("Player2 Surrender");
					}
					break;
				case "FIN":
					break;
			}
		}
	};

	ws.onopen = function() {
		console.log("Connect");
		changeTitle("Set Number");
	};

	ws.onerror = function (e) {
		console.log(e);
	};

	ws.onclose = function (e) {
		console.log(e);
	};
});

function winReset() {
	showMyNum(players[0].num);
	showYourNum(players[1].num);

	document.getElementById("my").children[0].classList.value = "cards";
	document.getElementById("your").children[0].classList.value = "cards";
}

function send() {
	ws.send($("#text").val());
}

let myNum = undefined;

function showMyNum(myNum) {
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

let player1Name = false;
let player2Name = false;
function toggleName() {
	togglePlayer1();
	togglePlayer2();
}
function togglePlayer1() {
	$("#player1").val((player1Name = !player1Name) ? players[0].name : "Player1");
}
function togglePlayer2() {
	$("#player2").val((player2Name = !player2Name) ? players[1].name : "Player2");
}

async function next() {
	location.href = "Watchlist";
}

function changeTitle(title) {
	$("#title").text(title);
}
