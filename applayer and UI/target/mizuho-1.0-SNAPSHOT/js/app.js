
var app=angular.module('mainapp',['ngRoute','ngCookies','ngMaterial', 'ngMessages','ngSanitize', 'ui.bootstrap','ngAnimate']);
	

app.config(function($routeProvider,$httpProvider){
	
//$httpProvider.defaults.withCredentials = true;
	
$routeProvider
 
	.when('/', {
		
		templateUrl:'pages/login.html',
		controller: 'loginCtrl'
	})     
	.when('/exporter_role', {
		
		templateUrl:'pages/exporter_role.html',
		controller: 'exporterCtrl'
	})
	.when('/importer_role',{
			
			templateUrl:'pages/importer_role.html',
			controller: 'exporterCtrl'
	})
	.when('/downloadpdf',{
			
			templateUrl:'pages/downloadpdf.html',
			controller: 'successCtrl'
	})
	.when('/insurance_company', {
		
		templateUrl:'pages/insurance_company.html',
		controller: 'exporterCtrl'
	})   
	.when('/shipping_company', {
		
		templateUrl:'pages/shipping_company.html',
		controller: 'exporterCtrl'
	}) 
	.when('/example', {
		
		templateUrl:'pages/example.html',
		controller: 'exampleCtrl'
	}) 
	.when('/exporter', {
		
		templateUrl:'pages/exporter.html',
		controller: 'exporterCtrl'
	}) 
	.when('/success', {
		
		templateUrl:'pages/success.html',
		controller: 'successCtrl'
	}) 		
	.when('/exporter_viewform',{

		templateUrl:'pages/exporter_viewform.html',
		controller: 'exporterCtrl'
	})

	.when('/importer_ed_form_view', {
		
		templateUrl:'pages/importer_ed_form_view.html',
		controller:'successCtrl'
	})
	
	.when('/exporter_doc', {
		
		templateUrl:'pages/exporter_doc.html',
		controller:'exporterCtrl'
	})

	.when('/importer_form_view', {
		
		templateUrl:'pages/importer_form_view.html',
		controller:'successCtrl'
	})

	.when('/importer_form',{
			
			templateUrl:'pages/importer_form.html',
			controller: 'successCtrl'
	})
    .when('/mizuho_header', {
		
		templateUrl:'pages/mizuho_header.html',
		controller:'successCtrl'
	})
	
		
});


app.factory('WebSocket',function($rootScope,$q) {

	var wsUri = "wss://9.122.79.81:9443/TradeFinanceApplicationServer/Notifications/";

    
    var output=[];
    var roleRequestCount = 0;

///testwebsocket starts here///

    $rootScope.testWebSocket=function() {
    	var websocket = new WebSocket(wsUri);


    websocket.onopen = function(){  
        console.log("Socket has been opened!"); 
        //websocket.doSend("/Role");
		};

   // websocket.doSend = function(message){
	    //websocket.send(message);
	   // console.log("sending role ");
	   // websocket.send(message);
	 	// };

	websocket.onmessage= function(evt){
	  	console.log("message");
	  	$rootScope.geteventdetails = evt.data;

	  	//console.log("type is "+ evt.data)
	  	console.log("EVENT is "+$rootScope.geteventdetails)
	  	alert("EVENT is "+$rootScope.geteventdetails)

	  	if(roleRequestCount == 0){
		    websocket.close();
		    return;
	    }
/*	    if(evt.data.startsWith("Role:")){
		    var roleStr = evt.data.substr(5);
		    if(roleRequestCount == 0)
		    {
			    websocket.close();
			    return;
		    }

		    }
		    else
		    {		    	
		    console.log("EVENT:"+ evt.data);
		    }*/
	 	}

  	websocket.onerror=function(){
  	 	console.log("ERROR:"+ evt.data);
  	 	}
  
	websocket.onclose=function(){
		websocket.close();
		console.log("on closed fntc called");

		if(roleRequestCount == 0)
		{
			roleRequestCount = 1;
			$rootScope.testWebSocket();
			return;
		}
		
		   // document.getElementById("connectionState").innerHTML = "DISCONNECTED";
	}

	}

    return output;

});

	   

/////

app.service('statusarray', function () {
	var status_array=[];
	 return status_array;
});

app.service('edlist', function () {
	var ed_list=[];
	 return ed_list;
});

app.service('ContractsList', function () {
	var listContracts=[];

	return listContracts;

});
app.service('StatusContractsList', function () {
	var listContractsStatus=[];

	//listContracts=listContracts.sort((a, b) => a - b);

	return listContractsStatus;

});

app.service('StatusList', function () {
	var listofstatus=[];

	//listContracts=listContracts.sort((a, b) => a - b);

	return listofstatus;

});


app.service('SharedDatacid', function () {
	var cid={no:'2'};
	 return cid;
});



app.service('SharedDataService', function () {

	
     var formData = {

       Sender:'',
	   Receiver:'',
	   Tag27:'',
	   Tag40A:'',
	   Tag20:'',
	   Tag31C:'',
	   Tag31D:'',
	   Tag50:'',
	   Tag59:'',
	   Tag32B:'',

	   Tag41A:'',
	   Tag42C:'',
	   Tag42D:'',
	   Tag43P:'',
	   Tag43T:'',
	   Tag44A:'',
	   Tag44B:'',
	   Tag44E:'',
	   Tag44F:'',
	   Tag44C:'',

	   Tag45A:'',
	   Tag46A:'',
	   Tag47A:'',
	   Tag71B:'',
	   Tag48:'',
	   Tag49:'',
	   Tag57D:''
	   

    };

    return formData;

});


app.service('JsonDataService', function () {

	
     var formData1 = {

        PAYER: '',
		PAYEE:'',
		TAX_REGISTRY_NO:'',
		INVOICE_CODE:'',
		INVOICE_NUMBER:'',
		PRINTING_NO:'',
		TOTAL_IN_WORDS:'',
		TOTAL_IN_FIGURES:'',
		PRINT_NO:'',
		ANTI_FORGERY_CODE:'',
		DATE_ISSUED:'',
		DUE_DATE:'',
		SHIPPING_DATE:'',
		LC_NUMBER:'',
		DATE_OF_PRESENTATION:'',
		CURRENCY:'',
		SCAC:'',
		BL_NO:'',
		BOOKING_NO:'',
		EXPORT_REFERENCES:'',
		SVC_CONTRACT:'',
		ONWARD_INLAND_ROUTING:'',
		SHIPPER_NAME_ADDRESS:'',
		CONSIGNEE_NAME_ADDRESS:'',
		VESSEL:'',
		VOYAGE_NO:'',
		PORT_OF_LOADING:'',
		PORT_OF_DISCHARGE:'',
		PLACE_OF_RECEIPT:'',
		PLACE_OF_DELIVERY:'',
		FREIGHT_AND_CHARGES:'',
		RATE:'',
		UNIT:'',
		CURRENCY:'',
		PREPAID:'',
		TOTAL_CONTAINERS_RECEIVED_BY_CARRIER:'',
		CONTAINER_NUMBER:'',
		PLACE_OF_ISSUE_OF_BL:'',
		NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS:'',
		DATE_OF_ISSUE_OF_BL:'',
		DECLARED_VALUE:'',
		SHIPPER_ON_BOARD_DATE:'',
		SIGNED_BY:'',
		LC_NUMBER:'',
		DATE_OF_PRESENTATION:'',
		CONSIGNEE_NAME:'',
		CONSIGNEE_ADDRESS:'',
		PACKING_LIST_NO:'',
		DATE:'',
		TOTAL_QUANTITY_MTONS:'',
		TOTAL_NET_WEIGHT_KGS:'',
		TOTAL_GROSS_WEIGHT_KGS:'',
		DELIVERY_TERMS:'',
		DOCUMENTARY_CREDIT_NUMBER:'',
		METHOD_OF_LOADING:'',
		CONTAINER_NUMBER:'',
		PORT_OF_LOADING:'',
		PORT_OF_DISCHARGE:'',
		DATE_OF_PRESENTATION:''
	   
    };

    return formData1;

});

			//-------------tab.////////////
app.controller('tabCtrl', function($scope,$location, $rootScope,$window){
	
	var currentTab=1;
	$scope.init=function()
	{
		$rootScope.check=0;
	}
	$scope.tabval=function(val){
		
		currentTab=val;
		if(val==1)
		{
			 //alert("came to value 1")
			var loginInfo1 = "userID=" + "user_type8_0" + "\npasscode=" + "ee72c14a8e" + "\nuserRole=ImporterBank";
			var loginInfo2 = "userID=" + "user_type2_0" + "\npasscode=" + "cabf781774" + "\nuserRole=ExporterBank";
			var loginInfo3 = "userID=" + "user_type4_0" + "\npasscode=" + "5437fb399a" + "\nuserRole=Importer";
			var loginInfo4 = "userID=" + "user_type1_0" + "\npasscode=" + "add24bff9a" + "\nuserRole=Exporter";
		var loginURL = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=Login";
		
		
		$rootScope.reload_val2=1;
		
			$location.path('/importer_bank');
			$.ajax({
			type: "POST",
			url: loginURL,
			
     crossDomain: true,
			async: true,
			data: loginInfo,
			contentType: "text/plain",
			dataType: "text",
			 
			success: function(data,status, xhr) {
				$window.location.reload();
				 
				 
				 if(!$scope.$$phase) {
         $scope.$apply();
    }
			},
			error: function(jqXHR, textStatus, errorThwrown) {
				
			}
		});
			
		}
			
				if(val==2)
				{
					
					var loginInfo1 = "userID=" + "user_type8_0" + "\npasscode=" + "ee72c14a8e" + "\nuserRole=ImporterBank";
					var loginInfo2 = "userID=" + "user_type2_0" + "\npasscode=" + "cabf781774" + "\nuserRole=ExporterBank";
					var loginInfo3 = "userID=" + "user_type4_0" + "\npasscode=" + "5437fb399a" + "\nuserRole=Importer";
					var loginInfo4 = "userID=" + "user_type1_0" + "\npasscode=" + "add24bff9a" + "\nuserRole=Exporter";
		var loginURL = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=Login";
					
					$location.path('/exporter_bank');
					$.ajax({
			type: "POST",
			url: loginURL,
			
     crossDomain: true,
			async: true,
			data: loginInfo2,
			contentType: "text/plain",
			dataType: "text",
			 
			success: function(data,status, xhr) {
				
			},
			error: function(jqXHR, textStatus, errorThwrown) {
				
			}
		});
					
				}
							
							if(val==3)
								$location.path('/importer');
							
									
										if(val==4)
								         $location.path('/exporter');
				   
				   
	}	
	
	
	$scope.isActiveTab = function(tabval) {
		
        return tabval == currentTab;
		 
    }
	
	
	});
    
		

	
	
	
