const _sleep = async (ms) => new Promise((resolve) => setTimeout(resolve, ms));

var ws;
let color;
let queue;
$(function() {
	ws = new WebSocket(url);

	ws.onmessage = async function(receive) {
		if(receive.data == "start") {
			//clearInterval(queue);
			clearInterval(color);
			$("#cancel").prop("disabled", true);
			$("#msg").text("Let's Game!!");
			await _sleep(1000);
			location.href = "Game";
		} else {
			console.log(receive.data);
			$("#queue").text(receive.data);
		}
	};

	ws.onopen = function() {
		console.log("Connect");
		randomColor();
		color = setInterval(randomColor, speed);
		//queue = setInterval(sendQueue, 1000);
	};

	ws.onclose = async function() {
		console.log("DisConnect");
		await _sleep(1000);
		console.log(color);
		if(!!color) {
			ws = new WebSocket(url);
		}
	};
	ws.onerror = function(e) {
		console.log(e);
	};
});

let randomColor = function() {
	document.getElementById("msg").style = "background-color: rgb(" + getRandom(0, 255) + "," + getRandom(0, 255) + "," + getRandom(0, 255) + ")";
};

let sendQueue = function() {
	ws.send("queue");
};
function getRandom( min, max ) {
	var random = Math.floor( Math.random() * (max + 1 - min) ) + min;
	return random;
}

function cancel() {
	location.href = "Mypage";
}