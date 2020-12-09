var UiView = {
  firstName: "Bill",
  lastName : "Gates",
  id : 648,
  clickfunction:function() {
  	},
  click: function(clickfunction) {
  		this.clickfunction = clickfunction;
   },
   doclick:function(){
        this.clickfunction();
   }
};
//view.click(function(){
//    alert("您好!");
//});