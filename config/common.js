function getTodayDate() {
	var myDate = new Date();
	var year = myDate.getFullYear();
	var month = myDate.getMonth();
	var da = myDate.getDate();
	month = parseInt(month) + 1;
	month = String(month);
	if (month.length == 1) {
		month = "0" + month;
	}
	da = String(da);
	if (da.length == 1) {
		da = "0" + da;
	}
	return String(year) + month + da;
}