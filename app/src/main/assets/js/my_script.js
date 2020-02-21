$(window).load(function () {

    var mas = document.getElementsByTagName('circle');

    var sbtOwner = 0;
	for(var i = 0; i < mas.length; i++) {
	    //если это не место, продолжим перебирать элементы
	    if (!mas[i].hasAttribute("sbt:seat")) continue;
	    //статус места
        var state = mas[i].getAttribute("sbt:state");
        //установим кликер только если место свободноеили предбронировано мной
        if (state == 1 || (state == 2 && mas[i].hasAttribute("sbt:owner"))) {
		    mas[i].onclick = function() {
		       addClickHandler(this);
		    }
        }
        if (state == 2 && mas[i].hasAttribute("sbt:owner")) sbtOwner = sbtOwner + 1;
	}
	if (sbtOwner > 0) Android.sbtOwner(sbtOwner);
})

function reserveOk(elementId) {
   var element = document.getElementById(elementId);
   element.removeAttribute("opacity");
}

function reserveFail(elementId) {
   var element = document.getElementById(elementId);
   var state = element.getAttribute("sbt:state");
   var class_ = element.getAttribute("class");
   element.removeAttribute("opacity");
   if (state == 1) {
     state = "2";
     element.setAttribute("class", class_ + " st4");
   } else {
     state = "1";
     element.setAttribute("class", class_.replace(" st4", ""));
   }
   element.setAttribute("sbt:state", state);
}

function addClickHandler(element) {

    var elementId = element.getAttribute("id");  //id элемента
    var seatId = element.getAttribute("sbt:id");  //id места в БС
    var state = element.getAttribute("sbt:state");//статус места
    var cat = element.getAttribute("sbt:cat");    //категория, описанная в svg документе
    var categoryName = findByAttributeValue("sbt:category", "sbt:index", cat).getAttribute("sbt:name"); //имя категории
    var categoryPrice = findByAttributeValue("sbt:category", "sbt:index", cat).getAttribute("sbt:price"); //цена категории
    var row = element.parentElement.getAttribute("sbt:row");    //номер ряда
    var seat = element.getAttribute("sbt:seat");                //номер места в ряду
    var sector = element.parentElement.getAttribute("sbt:sect");//название сектора

    //если уже нажимали на место и результат еще не вернулся
    if (element.hasAttribute("opacity")) return;

    element.setAttribute("opacity", "0.2");

    var class_ = element.getAttribute("class");
    if (state == 1) {
       state = "2";
       element.setAttribute("class", class_ + " st4");
   	   Android.showToastShort("Зарезервировано: место " + seat + ", " + row + ", сектор " + sector + ", цена " + categoryPrice + " руб.");
    }
    else {
       state = "1";
       element.setAttribute("class", class_.replace(" st4", ""));
       Android.showToastShort("Отмена резервирования места");
    }
    element.setAttribute("sbt:state", state);

    Android.reserve(seatId, state, elementId);
}

function findByAttributeValue(tagName, attribute, value)    {
  var all = document.getElementsByTagName(tagName);
  for (var i = 0; i < all.length; i++) {
    if (all[i].getAttribute(attribute) == value) { return all[i]; }
  }
}