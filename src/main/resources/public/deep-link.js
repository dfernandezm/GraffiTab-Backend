function getStarted() {
	// Get url.
	var url = window.location.href;

	// Build origin url.
	var arr = url.split("/");
	var result = arr[0] + "//" + arr[2];

	// Append path.
	result += '/getstarted';
	
	document.location = result;
}
