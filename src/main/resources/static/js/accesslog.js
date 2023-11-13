window.onload = () => {
	let reload = document.getElementById('reload');
	let page = document.getElementById('page');
	let size = document.getElementById('size');

	reload.onclick = () => {
		let params = [];
		params.push('page=' + page.value);
		params.push('size=' + size.value);

		window.location.href = "AccessLog?" + params.join("&");
	};

	let paramList = new Object;
	let param = location.search.substring(1).split('&');
	let paramSplit;
	for (let i = 0; param[i]; i++) {
		paramSplit = param[i].split('=');
		paramList[paramSplit[0]] = paramSplit[1];
	}
	if("page" in paramList) {
		page.value = paramList["page"];
	}
	if("size" in paramList) {
		size.value = paramList["size"];
	}
};