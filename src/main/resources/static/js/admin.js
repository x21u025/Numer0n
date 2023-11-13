window.addEventListener('load', function () {
	let column_no = 0; //今回クリックされた列番号
	let column_no_prev = 0; //前回クリックされた列番号
	document.querySelectorAll('#sort_table th').forEach(elm => {
		elm.onclick = function () {
			column_no = this.cellIndex; //クリックされた列番号
			let table = this.parentNode.parentNode.parentNode;
			let sortType = 0; //0:数値 1:文字
			let sortArray = new Array; //クリックした列のデータを全て格納する配列
			for (let r = 1; r < table.rows.length; r++) {
				//行番号と値を配列に格納
				let column = new Object;
				column.row = table.rows[r];
				column.value = table.rows[r].cells[column_no].textContent;
				sortArray.push(column);
				//数値判定
				if (isNaN(Number(column.value))) {
					sortType = 1; //値が数値変換できなかった場合は文字列ソート
				}
			}
			if (sortType == 0) { //数値ソート
				if (column_no_prev == column_no) { //同じ列が2回クリックされた場合は昇順ソート
					sortArray.sort(compareNumber);
				} else {
					sortArray.sort(compareNumberDesc);
				}
			} else { //文字列ソート
				if (column_no_prev == column_no) { //同じ列が2回クリックされた場合は降順ソート
					sortArray.sort(compareStringDesc);
				} else {
					sortArray.sort(compareString);
				}
			}
			//ソート後のTRオブジェクトを順番にtbodyへ追加（移動）
			let tbody = this.parentNode.parentNode;
			for (let i = 0; i < sortArray.length; i++) {
				tbody.appendChild(sortArray[i].row);
			}
			//昇順／降順ソート切り替えのために列番号を保存
			if (column_no_prev == column_no) {
				column_no_prev = -1; //降順ソート
			} else {
				column_no_prev = column_no;
			}
		};
	});

	let targets = document.querySelectorAll("input[type='checkbox'][name='hidden']");

	for (let target of targets) {
		target.addEventListener('change', function(event) {
			var XHR = new XMLHttpRequest();
			XHR.open("POST", "Admin", true);
			let data = {};
			data[event.target.value] = event.target.checked;
			console.log(EncodeHTMLForm(data));
			XHR.setRequestHeader( 'Content-Type', 'application/x-www-form-urlencoded' );
			XHR.send(EncodeHTMLForm(data));
		});
	}

	let checks = document.querySelectorAll("input[type='checkbox'][name='checkboxes']");
	for (let check of checks) {
		check.addEventListener('change', function() {
			reload();
		});
	}

	let hidden_check = document.querySelectorAll("input[type='checkbox'][name='checkboxes'][value='hidden']")[0];
	let pass_check = document.querySelectorAll("input[type='checkbox'][name='checkboxes'][value='pass']")[0];

	let paramList = new Object;
	let param = location.search.substring(1).split('&');
	let paramSplit;
	for (let i = 0; param[i]; i++) {
		paramSplit = param[i].split('=');
		paramList[paramSplit[0]] = paramSplit[1];
	}
	if("hidden" in paramList) {
		hidden_check.checked = toBoolean(paramList["hidden"]);
	}
	if("pass" in paramList) {
		pass_check.checked = toBoolean(paramList["pass"]);
	}
});
//数値ソート（昇順）
function compareNumber(a, b)
{
	return a.value - b.value;
}
//数値ソート（降順）
function compareNumberDesc(a, b)
{
	return b.value - a.value;
}
//文字列ソート（昇順）
function compareString(a, b) {
	if (a.value < b.value) {
		return -1;
	} else {
		return 1;
	}
	return 0;
}
//文字列ソート（降順）
function compareStringDesc(a, b) {
	if (a.value > b.value) {
		return -1;
	} else {
		return 1;
	}
	return 0;
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

function reload() {
	let checks = document.querySelectorAll("input[type='checkbox'][name='checkboxes']");

	let params = [];
	for(let check of checks) {
		params.push(check.value + "=" + check.checked);
	}

	window.location.href = "Admin?" + params.join("&");
}

function toBoolean(data) {
  return data.toLowerCase() === 'true';
}