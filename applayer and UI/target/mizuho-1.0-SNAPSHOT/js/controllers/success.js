//------------1.Success cntrl Controller--------//
app.controller('successCtrl', function($scope,$location,$rootScope,StatusContractsList,SharedDataService,SharedDatacid,ContractsList,StatusList,edlist,statusarray,$window,$http,$cookieStore,WebSocket){
	console.log("yes in Importer bank page");

	$rootScope.websocket = WebSocket;
	//alert($scope.listEvents);
	$rootScope.testWebSocket();

	$rootScope.status_val = false;
	

	$scope.listContractsStatus = StatusContractsList;
	$scope.listContracts = [];
	console.log($scope.listContracts);
	$scope.status_array = statusarray;
	$scope.ed_list = edlist;
	$scope.listofstatus = StatusList;
	$scope.formData = SharedDataService;
    $scope.cid = SharedDatacid;
	//console.log($scope.listContracts)
//      $scope.list = $scope.$parent.personList

    $scope.getrolenaame = localStorage.getItem('setrolename');
	$scope.getroleid = localStorage.getItem('setroleid');
	$scope.getusername = localStorage.getItem('setusername');

    $scope.listDetails = [];

    if(localStorage.getItem('setrolename')==""){
       // alert("Please Login");
        $location.path('/');
    }
    
	$rootScope.imp_form = {
		Sender: "",
		Receiver :"",
		Tag27 :"",
		Tag40A :"",
		Tag20 :"",
		Tag31C :"",
		Tag31D :"",
		Tag50 :"",
		Tag59 :"",
		Tag32B :"",
		Tag41A :"",
		Tag42C :"",
		Tag42D :"",
		Tag43P :"",
		Tag43T :"",
		Tag44A :"",
		Tag44B :"",
		Tag44E :"",
		Tag44F :"",
		Tag44C :"",
		Tag45A:"",
		Tag46A:"",
		Tag47A:"", 
		Tag71B:"",
		Tag48 :"",
		Tag49 :"", 
		Tag57D :""     
       
   };

	
	$scope.checkStatus = function(x) {
	if((x=="SUBMITTED_ED_BY_EB")||(x=="ACCEPTED_ED_BY_IB")){
	console.log(x);
	return false;

	}

	else
	return true;

	};

	$scope.roleHide = function() {

	console.log(localStorage.getItem('setrolename'));
	if(localStorage.getItem('setrolename')=="Importer Bank"){
	return false;    
	}
	else
	if(localStorage.getItem('setrolename')=="Exporter Bank" || localStorage.getItem('setrolename')=="Exporter" || localStorage.getItem('setrolename')=="Importer" || localStorage.getItem('setrolename')=="Shipping Company" || localStorage.getItem('setrolename')=="Insurance Company"){
	return true;  
	}
	}

  	$scope.movetopdfpage = function(){

  		$scope.getcontractforfile = localStorage.getItem('setcontractnumber');
  		console.log($scope.getcontractforfile);

  		$location.path('/downloadpdf');	

		window.downloadPDF1= function downloadPDF1() {

		var invoiceLnk = document.getElementById('invoiceLnk');
		invoiceLnk.href = $rootScope.invoice_view;

		invoiceLnk.click();
		
		event.preventDefault();

		}

		window.downloadPDF2 = function downloadPDF2() {

		var boldLnk = document.getElementById('boldLnk');
		boldLnk.href = $rootScope.billoflading_view;

		boldLnk.click();
		event.preventDefault();

		}


		window.downloadPDF3 = function downloadPDF3() {

		var plLnk = document.getElementById('plLnk');
		plLnk.href = $rootScope.packaginglist_view;

		plLnk.click();
		event.preventDefault();

		
		}


  		$.ajax({
		type: "GET",
		url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$scope.getcontractforfile+"&docType=INVOICE&docFormat=PDF",
		async: true,
		contentType: "text/plain",
		dataType: "text",
		success: function(response) {

		//console.log(response);
		$rootScope.invoice_view=response;
		localStorage.setItem('setinvoiceval', $rootScope.invoice_view);
		console.log("result1 :"+$rootScope.invoice_view);
		
		},
		error: function(jqXHR, textStatus, errorThwrown) {
		alert("error");
		}
	});		

  		$.ajax({
		type: "GET",
		url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$scope.getcontractforfile+"&docType=BL&docFormat=PDF",
		async: true,
		contentType: "text/plain",
		dataType: "text",
		success: function(response) {

			//$scope.$apply();
			$rootScope.billoflading_view=response;
			console.log("result 2:"+$rootScope.billoflading_view);
			
			//$scope.show_billoflng_box=$scope.chkStatus2;
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
		});		

		$.ajax({
		type: "GET",
		url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$scope.getcontractforfile+"&docType=PACKINGLIST&docFormat=PDF",
		async: true,
		contentType: "text/plain",
		dataType: "text",
		success: function(response) {

			//$scope.$apply();
			$rootScope.packaginglist_view=response;
			console.log("result3 :"+$rootScope.packaginglist_view);
			
			//$scope.show_pcknglist_box=$scope.chkStatus3;
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
		});	




  	}
	
	 var loginurl2 = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=listContractsByRoleName&role="+$scope.getusername+"&roleID="+$scope.getroleid;

/////////////
/////////////LIST OF CONTRACTS
/////////////
			$.ajax({
			type: "GET",
			url: loginurl2,
			async: true,
			//data: JSON.stringify($scope.formData),
			contentType: "application/json",
			dataType: "text",
			success: function(response) {
					
				console.log("list is coming");

				if($scope.reload_val==1)
				{
				$window.location.reload();
				$scope.reload_val=0;
				}
				console.log(response);
						
				for(i=0;i<JSON.parse(response).contracts.length;i++)
				{
					// $scope.listContracts[i]=JSON.parse(response).contracts[i].contractID;
					// $scope.listContractsStatus[i]=JSON.parse(response).contracts[i].contractStatus;
					 $scope.listContracts[i]=JSON.parse(response).contracts[i].contractID;
					 $scope.listDetails[i]=JSON.parse(response).contracts[i];
					
				}
				//console.log(JSON.parse(response).contracts[i].contractID)
				console.log("data"+$scope.listContracts);				
				console.log("data"+$scope.listContractsStatus);

				if($scope.listContractsStatus == 'PAYMENT_DUE_FROM_IB_TO_EB'){
					//$rootScope.status_val = false;
				}

				$scope.$apply();	
				//alert("data"+$scope.listContracts);
			},
			error: function(data) {
				console.log("list is not coming");
				
			}
			});	 
/*
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			console.log("error")	
			}
		});		 */

			$scope.backtosuccess = function(){

			//	alert("Role when clicked is:"+ $scope.getrolenaame);


				if($scope.getrolenaame == "Importer Bank"){
					$location.path('/success');				
				}
				else 
					if($scope.getrolenaame == "Importer"){
						$location.path('/importer_role');	
					}
				else if($scope.getrolenaame == "Exporter"){
					$location.path('/exporter_role');
					}
				else if($scope.getrolenaame == "Shipping Company"){
					$location.path('/shipping_company');
				}
				else if($scope.getrolenaame == "Insurance Company"){
					$location.path('/insurance_company');
				}
				else if($scope.getrolenaame == "Exporter Bank"){
					$location.path('/exporter');
				}
				}


///////////////////////////////Submit LC BY Importer Bank /////////////////////////////////////////////////////////////////////////

		$scope.submitForm = function() {
			//console.log("submit lc clked")
			//alert("clked");
		$scope.formData.Sender =  $rootScope.imp_form.Sender;
		$scope.formData.Tag57D =  $rootScope.imp_form.Tag57D;
		$scope.formData.Receiver =  $rootScope.imp_form.Receiver;
		$scope.formData.Tag27 =  $rootScope.imp_form.Tag27;
		$scope.formData.Tag40A =  $rootScope.imp_form.Tag40A;
		$scope.formData.Tag20 =  $rootScope.imp_form.Tag20;
		$scope.formData.Tag31C =  $rootScope.imp_form.Tag31C;
		$scope.formData.Tag31D =  $rootScope.imp_form.Tag31D;
		$scope.formData.Tag50 =  $rootScope.imp_form.Tag50;
		$scope.formData.Tag59 =  $rootScope.imp_form.Tag59;
		$scope.formData.Tag32B =  $rootScope.imp_form.Tag32B;
		$scope.formData.Tag41A =  $rootScope.imp_form.Tag41A;
		$scope.formData.Tag42C =  $rootScope.imp_form.Tag42C;
		$scope.formData.Tag42D =  $rootScope.imp_form.Tag42D;
		$scope.formData.Tag43P =  $rootScope.imp_form.Tag43P;
		$scope.formData.Tag43T =  $rootScope.imp_form.Tag43T;
		$scope.formData.Tag44A =  $rootScope.imp_form.Tag44A;
		$scope.formData.Tag44B =  $rootScope.imp_form.Tag44B;
		$scope.formData.Tag44E =  $rootScope.imp_form.Tag44E;
		$scope.formData.Tag44F =  $rootScope.imp_form.Tag44F;
		$scope.formData.Tag44C =  $rootScope.imp_form.Tag44C;
		$scope.formData.Tag45A =  $rootScope.imp_form.Tag45A;
		$scope.formData.Tag46A =  $rootScope.imp_form.Tag46A;
		$scope.formData.Tag47A =  $rootScope.imp_form.Tag47A;
		$scope.formData.Tag71B =  $rootScope.imp_form.Tag71B;
		$scope.formData.Tag48 =  $rootScope.imp_form.Tag48;
		$scope.formData.Tag49 =  $rootScope.imp_form.Tag49;
		$scope.formData.Tag57D  =  $rootScope.imp_form.Tag57D;

		console.log($scope.formData);
		//console.log($rootScope.imp_form.Sender);

		var submitlc = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=submitLC&ebID=bk&iID="+$scope.formData.Tag50+"&eID="+$scope.formData.Tag59;

		
		if(($rootScope.imp_form.Sender == "") || ($rootScope.imp_form.Receiver == "") ||  ( $rootScope.imp_form.Tag27 == "") ||
			($rootScope.imp_form.Tag40A == "") ||($rootScope.imp_form.Tag20 == "") ||($rootScope.imp_form.Tag31C == "")||( $rootScope.imp_form.Tag31D == "" )||
			($rootScope.imp_form.Tag50 == "" )||($rootScope.imp_form.Tag59 == "" )||($rootScope.imp_form.Tag32B == "" )||($rootScope.imp_form.Tag41A == "" ) ||
			( $rootScope.imp_form.Tag42C == "") ||($rootScope.imp_form.Tag42D == "")||( $rootScope.imp_form.Tag43P == "")||($rootScope.imp_form.Tag43T == "")||
			($rootScope.imp_form.Tag44A == "")||($rootScope.imp_form.Tag44B == "" )||($rootScope.imp_form.Tag44E == "" )||($rootScope.imp_form.Tag44F == "") ||
			($rootScope.imp_form.Tag44C == "" ) ||($rootScope.imp_form.Tag45A == "")||($rootScope.imp_form.Tag46A == "")||($rootScope.imp_form.Tag47A == "") ||
			($rootScope.imp_form.Tag71B == "") || ($rootScope.imp_form.Tag48 == "") || ($rootScope.imp_form.Tag49 == "") || ($rootScope.imp_form.Tag57D == ""))
			{ 
				alert("Please fill all the L/C Details");
			}           

			else{
				// alert("hiii");
				console.log("all fields are filled");

				$.ajax({
				type: "POST",
				url: submitlc,
				async: true,
				data: JSON.stringify($scope.formData),
				contentType: "application/json",
				dataType: "text",
				success: function(response) {
				console.log(response);
				//console.log(JSON.parse(response.contracts.contractID));

				///$scope.getidofcontract = JSON.parse(response.contracts.contractID);

				alert("LC created Successfully");

				$location.path('/success');	
				$scope.$apply();
				$window.location.reload();

				},
				error: function(jqXHR, textStatus, errorThwrown) {
				console.log("Error");
				}

				});
			}
	/*	else {     	
				$.ajax({
				type: "POST",
				url: submitlc,
				async: true,
				data: JSON.stringify($scope.formData),
				contentType: "application/json",
				dataType: "text",
				success: function(response) {
				console.log(response);
				console.log(JSON.parse(response.contracts.contractID))
				alert("LC created Successfully -"+JSON.parse(response.contracts.contractID);
				$location.path('/success');	
				$scope.$apply();
				$window.location.reload();

				},
				error: function(jqXHR, textStatus, errorThwrown) {
				console.log("Please fill all the L/C Details");
				}
				});
			}*/
		}
////////////////////////
/*get the view form lc details*/
/////////////////////////

			$scope.view_lc_form=function(x){
			
			console.log(x)
			console.log($scope.listContracts.sort());

			//$rootScope.lc_form_index=x;
			$rootScope.contract_no=$scope.listContracts[x];
			//$location.path('/importer_form_view');
			console.log($rootScope.contract_no);

			var loginInfo = "userID=" + "user_type8_0" + "\npasscode=" + "ee72c14a8e" + "\nuserRole=ImporterBank";

				$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID="+$rootScope.contract_no,
			async: true,
			//data: JSON.stringify($scope.formData),
			contentType: "application/json",
			dataType: "text",
			success: function(response) {
						
				$rootScope.imp_form_view=JSON.parse(response);
				//alert("json"+$rootScope.imp_form_view);]
				console.log($rootScope.imp_form_view);
				console.log($rootScope.imp_form_view.Receiver);
				$location.path('/importer_form_view');
				// if(!$scope.$$phase) {
				$scope.$apply();
				// 	}
				},
			error: function(jqXHR, textStatus, errorThwrown) {
				alert("error: LC form details are not coming");
			}
			});


/*get the get lc form status*/
			$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLCStatus&contractID="+$rootScope.contract_no,
			async: true,
			//data: JSON.stringify($scope.formData),
			contentType: "application/json",
			dataType: "text",
			success: function(response) {
	            console.log(response);
	 
	            $rootScope.statusoflc=JSON.parse(response).Status;
				console.log($rootScope.statusoflc);
				$scope.$apply(); 
			},
			error: function() {
            alert("lC form status is not coming")
          	}
       		});

/*get the get lc form status funtn ends....*/

		
     }

////////////////////////
/*get the view form lc details function ends here */
/////////////////////////
		function getstatusoflc(id){
			
			$.ajax({
					type: "GET",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLCStatus&contractID="+id,
					async: true,
					//data: JSON.stringify($scope.formData),
					contentType: "application/json",
					dataType: "text",
					success: function(response) {
						console.log("checking the status");
						console.log(response);

						$rootScope.show_status=JSON.parse(response).Status
						console.log($rootScope.show_status);
						$scope.$apply();

						//console.log($rootScope.listofstatus);
						//$rootScope.listofstatus.push($rootScope.show_status);

						//console.log($rootScope.listofstatus.push($rootScope.show_status));


					},
					error: function(data) {
						console.log("status not coming");
			
					}
				});
		}

//call call form//
			$scope.callform = function(){
			$location.path('/importer_form');
			

			}

//logout fntn
			$scope.movetologinpage=function(){

			$.ajax({
				type: "POST",
				url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=logout",

				crossDomain: true,
				async: true,
				//data: loginInfo4,
				contentType: "text/plain",
				dataType: "text",

				success: function(data,status, xhr) {
						console.log("logout successfully from importer bank")
						$location.path('/');
						$scope.$apply();
				},
				error: function(jqXHR, textStatus, errorThwrown) {
					console.log("Error: in logout")
				}
			});

			}
/*close the get n view lc form details button*/
			$scope.close_imp_form_view=function(){
			
				if($scope.getrolenaame == "Importer Bank"){
					$location.path('/success');				
					}
					else 
					if($scope.getrolenaame == "Importer"){
					$location.path('/importer_role');	
					}
					else if($scope.getrolenaame == "Exporter"){
					$location.path('/exporter_role');   
					}
					else if($scope.getrolenaame == "Shipping Company"){
					$location.path('/shipping_company');
					}   
					else if($scope.getrolenaame == "Insurance Company"){
					$location.path('/insurance_company');
					}
					else if($scope.getrolenaame == "Exporter Bank"){
					$location.path('/exporter');
					} 
				
			}
			
///////view ed form as importer////////
		$scope.view_ed_form=function(x,u){

		console.log(x)
		console.log($scope.listContracts.sort());
		$rootScope.contract_no=$scope.listContracts[x];
		console.log($rootScope.contract_no);

		localStorage.setItem('setcontractnumber', $rootScope.contract_no);


		var status2="";
            status2=u;
                if((status2=="SUBMITTED_ED_BY_EB")||(localStorage.getItem('setrolename')=="Importer")){
                    $rootScope.iacc_btn=false;
                    $rootScope.irej_btn=false;
                }
                else if((status2=="SUBMITTED_ED_BY_EB")||(localStorage.getItem('setrolename')=="Exporter")){
                    $rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true;
                }
                else if(status2=="ACCEPTED_ED_BY_IB"){
                    $rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true;
                }
                else if(status2=="REJECTED_ED_BY_IB"){
                    $rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true;
                }
                else
                    if((localStorage.getItem('setrolename'))=="Exporter Bank")
                    {
                    $rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true;
                    }

                $.ajax({
    				type: "GET",
    				url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID="+$rootScope.contract_no, //(https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID='+$rootScope.contract_no) ,
    				async: true,

    				contentType: "application/json",
    				dataType: "text",
    				success: function(response) {


    				$rootScope.imp_form_view=JSON.parse(response);

    				console.log($rootScope.imp_form_view.Receiver);
    				$location.path('/importer_ed_form_view');
    				$scope.$apply();				
    				},
    				error: function(jqXHR, textStatus, errorThwrown) {
    				alert("error");
    				}
    				});
	//INVOICE
			$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=INVOICE&docFormat=JSON",
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {
			//console.log(response);	
			$rootScope.getinvoicejsonval = JSON.parse(response);
			console.log($rootScope.getinvoicejsonval)		
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
			});	
	//BILL OF LADING

			$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=BL&docFormat=JSON",
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {
			$rootScope.getbljsonval = JSON.parse(response);
			console.log($rootScope.getbljsonval)				
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
			});	

	//PACKAGING LIST

			$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=PACKINGLIST&docFormat=JSON",
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {
			$rootScope.getpackingjsonval = JSON.parse(response);
			console.log($rootScope.getpackingjsonval)					
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
			});	



			}
/////////////////////////////////
		$scope.accept_ed_form=function(x){

			console.log("id is"+x);

			

						///alert("clicked")
					$.ajax({
					type: "POST",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=acceptED&contractID="+x,
					async: true,

					contentType: "application/json",
					dataType: "text",
					success: function(response) {

					$rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true

					alert("Export documents accepted, Workflow completed");
					$location.path('/success');
					$scope.$apply();


					},
					error: function() {
					alert("error");

					}
					});
                     $scope.creat_ed2=true;

		}
		
		$scope.reject_ed_form=function(x){

			console.log("id is"+x);

				///alert("clicked")
					$.ajax({
					type: "POST",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=rejectED&contractID="+x,
					async: true,

					contentType: "application/json",
					dataType: "text",
					success: function(response) {

					$rootScope.iacc_btn=true;
                    $rootScope.irej_btn=true

					alert("Export documents Rejected");
					$location.path('/success');
					$scope.$apply();


					},
					error: function() {
					alert("error");

					}
					});

		}


});

//Success cntrl ends//


//----------------2. Form Control Controller ------------//

app.controller('FormCtrl', function ($scope,$location, $http,$rootScope,SharedDataService,SharedDatacid,$window) {
	console.log("yes in form controller")

	
});
//form contrl ends///



	//------------3.Exporter cntrl Controller--------//



app.controller('exporterCtrl',function($scope,$location,$rootScope,StatusContractsList,JsonDataService,SharedDatacid,SharedDataService,ContractsList,edlist,statusarray,$window,$http,$cookieStore){
	console.log("yes in Exporter bank page");

	$rootScope.form = {
			PAYER: "",
			PAYEE:"",
			TAX_REGISTRY_NO:"",
			INVOICE_CODE:"",
			INVOICE_NUMBER:"",
			PRINTING_NO:"",
			TOTAL_IN_WORDS:"",
			TOTAL_IN_FIGURES:"",
			PRINT_NO:"",
			ANTI_FORGERY_CODE:"",
			DATE_ISSUED:"",
			DUE_DATE:"",
			SHIPPING_DATE:"",
			LC_NUMBER:"",
			DATE_OF_PRESENTATION:"",
			CURRENCY:"",
			SCAC:"",
			BL_NO:"",
			BOOKING_NO:"",
			EXPORT_REFERENCES:"",
			SVC_CONTRACT:"",
			ONWARD_INLAND_ROUTING:"",
			SHIPPER_NAME_ADDRESS:"",
			CONSIGNEE_NAME_ADDRESS:"",
			VESSEL:"",
			VOYAGE_NO:"",
			PORT_OF_LOADING:"",
			PORT_OF_DISCHARGE:"",
			PLACE_OF_RECEIPT:"",
			PLACE_OF_DELIVERY:"",
			FREIGHT_AND_CHARGES:"",
			RATE:"",
			UNIT:"",
			CURRENCY:"",
			PREPAID:"",
			TOTAL_CONTAINERS_RECEIVED_BY_CARRIER:"",
			CONTAINER_NUMBER:"",
			PLACE_OF_ISSUE_OF_BL:"",
			NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS:"",
			DATE_OF_ISSUE_OF_BL:"",
			DECLARED_VALUE:"",
			SHIPPER_ON_BOARD_DATE:"",
			SIGNED_BY:"",
			LC_NUMBER:"",
			DATE_OF_PRESENTATION:"",
			CONSIGNEE_NAME:"",
			CONSIGNEE_ADDRESS:"",
			PACKING_LIST_NO:"",
			DATE:"",
			TOTAL_QUANTITY_MTONS:"",
			TOTAL_NET_WEIGHT_KGS:"",
			TOTAL_GROSS_WEIGHT_KGS:"",
			DELIVERY_TERMS:"",
			DOCUMENTARY_CREDIT_NUMBER:"",
			METHOD_OF_LOADING:"",
			CONTAINER_NUMBER:"",
			PORT_OF_LOADING:"",
			PORT_OF_DISCHARGE:"",
			DATE_OF_PRESENTATION:"",
		}
	if(localStorage.getItem('setrolename')==""){
        //alert("Please Login");
        $location.path('/');
    }


	console.log($cookieStore.get('user_name'));
	$scope.getnofromcookie = $cookieStore.get('user_name');

   	$scope.checkStatus = function(x) {

	if((x=="SUBMITTED_ED_BY_EB")||(x=="ACCEPTED_ED_BY_IB")){ 
	return false;
	}
		
	else
	return true;
		
  	};
    
    console.log($rootScope.creat_ed);

	

	$scope.getrolenaame = localStorage.getItem('setrolename');
	$scope.getroleid = localStorage.getItem('setroleid');
	$scope.getusername = localStorage.getItem('setusername');
	console.log($scope.getrolenaame);
    
      $scope.firstReset = function(form) {
//          form.PAYER =null;
//          form.PAYEE =null;
          angular.copy({},form);
  }

    $scope.listDetails = [];
    
    $scope.formData1 = JsonDataService;
	$scope.listContractsStatus = StatusContractsList;
	$scope.listContracts = [];
	$scope.status_array = statusarray;
	$scope.ed_list = edlist;
    $scope.creat_ed2=false;
		
	$scope.upload_btn=true;

	 $scope.formData = SharedDataService;
	 $scope.cid = SharedDatacid;
	 $scope.data = {}; 

	 $cookieStore.put('user_name', $rootScope.contract_no);

	 
	 $scope.close_imp_form_view=function(){
			
			if($scope.getrolenaame == "Importer Bank"){
				$location.path('/success');				
				}
				else 
				if($scope.getrolenaame == "Importer"){
				$location.path('/importer_role');	
				}
				else if($scope.getrolenaame == "Exporter"){
				$location.path('/exporter_role');   
				}
				else if($scope.getrolenaame == "Shipping Company"){
				$location.path('/shipping_company');
				}   
				else if($scope.getrolenaame == "Insurance Company"){
				$location.path('/insurance_company');
				}
				else if($scope.getrolenaame == "Exporter Bank"){
				$location.path('/exporter');
				} 
			
		}
	 
	 $scope.backtosuccess = function(){

			if($scope.getrolenaame == "Importer Bank"){
				$location.path('/success');				
			}
			else 
				if($scope.getrolenaame == "Importer"){
					$location.path('/importer_role');	
				}
			else if($scope.getrolenaame == "Exporter"){
				$location.path('/exporter_role');
				}
			else if($scope.getrolenaame == "Shipping Company"){
				$location.path('/shipping_company');
			}
			else if($scope.getrolenaame == "Insurance Company"){
				$location.path('/insurance_company');
			}
			else if($scope.getrolenaame == "Exporter Bank"){
				$location.path('/exporter');
			}
			}


//	$scope.submit_ed =function(contract_no){
//		
//	$scope.upload_btn=false;
//	alert("Export documents submitted successfully");
//	$location.path('/exporter');
//	}

/*close the get n view lc form details button*/
	



			$scope.view_ed_form=function(x){

			console.log(x)
			console.log($scope.listContracts.sort());
			$rootScope.contract_no=$scope.listContracts[x];
			console.log($rootScope.contract_no);

			localStorage.setItem('setcontractnumber', $rootScope.contract_no);
			if((localStorage.getItem('setrolename'))=="Importer")
            {     
            //$rootScope.creat_ed=true;
            $rootScope.iacc_btn=true;
            $rootScope.irej_btn=true;
            }
        else
            if((localStorage.getItem('setrolename'))=="Exporter")
            {

            $rootScope.creat_ed=true;
            $rootScope.iacc_btn=true;
            $rootScope.irej_btn=true;
            }
        else
            if((localStorage.getItem('setrolename'))=="Insurance Company")
            {

            $rootScope.creat_ed=true;
            $rootScope.iacc_btn=true;
            $rootScope.irej_btn=true;
            }
          else
            if((localStorage.getItem('setrolename'))=="Shipping Company")
            {

            $rootScope.creat_ed=true;
            $rootScope.iacc_btn=true;
            $rootScope.irej_btn=true;
            }
            else
                if((localStorage.getItem('setrolename'))=="Exporter Bank")
                {
                $rootScope.iacc_btn=true;
                $rootScope.irej_btn=true;
                }
			
			$.ajax({
				type: "GET",
				url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID="+$rootScope.contract_no, //(https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID='+$rootScope.contract_no) ,
				async: true,

				contentType: "application/json",
				dataType: "text",
				success: function(response) {


				$rootScope.imp_form_view=JSON.parse(response);

				console.log($rootScope.imp_form_view.Receiver);
				$location.path('/importer_ed_form_view');
				$scope.$apply();				
				},
				error: function(jqXHR, textStatus, errorThwrown) {
				alert("error");
				}
				});
			
			
			//INVOICE
					$.ajax({
					type: "GET",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=INVOICE&docFormat=JSON",
					async: true,

					contentType: "application/json",
					dataType: "text",
					success: function(response) {
					//console.log(response);	
					$rootScope.getinvoicejsonval = JSON.parse(response);
					console.log($rootScope.getinvoicejsonval)		
					},	
					error: function(jqXHR, textStatus, errorThwrown) {
					alert("error");
					}
					});	
			//BILL OF LADING

					$.ajax({
					type: "GET",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=BL&docFormat=JSON",
					async: true,

					contentType: "application/json",
					dataType: "text",
					success: function(response) {
					$rootScope.getbljsonval = JSON.parse(response);
					console.log($rootScope.getbljsonval)				
					},
					error: function(jqXHR, textStatus, errorThwrown) {
					alert("error");
					}
					});	

			//PACKAGING LIST

					$.ajax({
					type: "GET",
					url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getED&contractID="+$rootScope.contract_no+"&docType=PACKINGLIST&docFormat=JSON",
					async: true,

					contentType: "application/json",
					dataType: "text",
					success: function(response) {
					$rootScope.getpackingjsonval = JSON.parse(response);
					console.log($rootScope.getpackingjsonval)					
					},
					error: function(jqXHR, textStatus, errorThwrown) {
					alertrt("error");
					}
					});	
			}	         
	///////////////////////FILE HANDLING STARTS HERE//////////////////////////

	/*fIRST FILE*/

		$scope.click1 = function() { //default function, to be override if browser supports input type='file'
		$scope.data.alert = "Your browser doesn't support HTML5 input type='File'"
		}

		var fileSelect1 = document.createElement('input'); //input it's not displayed in html, I want to trigger it form other elements
		fileSelect1.type = 'file';

		if (fileSelect1.disabled) { //check if browser support input type='file' and stop execution of controller
		return;
		}

		$scope.click1 = function() { //activate function to begin input file on click
		fileSelect1.click();
		}

		fileSelect1.onchange = function() { //set callback to action after choosing file
		var f = fileSelect1.files[0], r = new FileReader();
		console.log(f.name);
		console.log(f.size);
		$scope.invoicefilename=f.name;
		$scope.invoicefilesize=f.size +"bytes";

		r.onloadend = function(e) { //callback after files finish loading
		$scope.data.b64 = e.target.result;
		//console.log(files[0].name);

		$rootScope.invoicedata = $scope.data.b64;

		}

		r.readAsDataURL(f); //once defined all callbacks, begin reading the file
		//console.log($scope.data.b64 );

		};
    
/*SECOND FILE*/

		$scope.click2 = function() { 
		$scope.data.alert = "Your browser doesn't support HTML5 input type='File'"
		}

		var fileSelect2 = document.createElement('input'); 
		fileSelect2.type = 'file';

		if (fileSelect2.disabled) { 
		return;
		}

		$scope.click2 = function() { 
		fileSelect2.click();
		}

		fileSelect2.onchange = function() { 
		var f = fileSelect2.files[0], r = new FileReader();
		console.log(f.name);
		$scope.bolfilename=f.name;
		$scope.bolfilesize=f.size;

		r.onloadend = function(e) { 
		$scope.data.b64 = e.target.result;

		$rootScope.BOLdata =$scope.data.b64;
		//$scope.$apply();
		//console.log($rootScope.BOLdata); 

		}

		r.readAsDataURL(f); 

		};

/*THIRD FILE*/

		$scope.click3 = function() { 
		$scope.data3.alert = "Your browser doesn't support HTML5 input type='File'"
		}

		var fileSelect3 = document.createElement('input'); 
		fileSelect3.type = 'file';

		if (fileSelect3.disabled) { 
		return;
		}

		$scope.click3 = function() { 
		fileSelect3.click();
		}

		fileSelect3.onchange = function() { 
		var f = fileSelect3.files[0], r = new FileReader();
		console.log(f.name);
		$scope.plfilename=f.name;
		$scope.plfilesize=f.size;

		r.onloadend = function(e) { 
		$scope.data.b64 = e.target.result;

		$rootScope.BOEXdata= $scope.data.b64;
		$scope.$apply();
		//console.log($rootScope.BOEXdata); 
		}

		r.readAsDataURL(f); 
		};


/*UPLOAD PDF*/

		$scope.uploadPdf = function(getinsuranceval,getshippingval){

		console.log("clked to upload pdf ed-docs");
		console.log($cookieStore.get('user_name'));

		$scope.getnofromcookie = $cookieStore.get('user_name');

		console.log($scope.getnofromcookie); 

		$scope.upload_btn=false;
        $scope.creat_ed2=true;

        console.log(getinsuranceval);

		 var pdf_data = {
		Invoice:{},
		InvoicePDF: $rootScope.invoicedata,
		BillOfLading: {},
		BillOfLadingPDF: $rootScope.BOLdata,
		PackagingList:{},
		PackagingListPDF: $rootScope.BOEXdata,
		InsuranceConpany: getinsuranceval,
		ShippingCompany: getshippingval
		};

		console.log(pdf_data);

		
		var loginURL = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=login";

	
			$.ajax({
			type: "POST",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=submitED&contractID="+$scope.getnofromcookie,
			type: "POST",
			async: true,
			data: JSON.stringify(pdf_data),
			dataType: "json",
			contentType: "application/json",
			success: function (response) {
				alert("Export Documents Submitted Successfully");
				$location.path('/exporter');
				$scope.$apply();
				$window.location.reload();
			},
			error: function(jqXHR, textStatus, errorThwrown){
			alert("The upload has been canceled by the user or the browser dropped the connection.")
			console.log("error");
			}

			});
		
		
		//console.log($cookieStore.get('user_name'));

		}	
	////////////////////////FILES CODING ENDS HERE/////////////////////////

		console.log($scope.getusername);
	    console.log($scope.getroleid);
   var loginurl1 ="https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=listContractsByRoleName&role="+$scope.getusername+"&roleID="+$scope.getroleid;

/////////////
/////////////LIST OF CONTRACTS
/////////////

		$.ajax({
			type: "GET",
			url: loginurl1,
			async: true,
			//data: JSON.stringify($scope.formData),
			contentType: "application/json",
			dataType: "text",
			success: function(response) {
					
				console.log("list is coming");

				if($scope.reload_val==1)
				{
				$window.location.reload();
				$scope.reload_val=0;
				}
				console.log(response);
						
				for(i=0;i<JSON.parse(response).contracts.length;i++)
				{
					// $scope.listContracts[i]=JSON.parse(response).contracts[i].contractID;
					// $scope.listContractsStatus[i]=JSON.parse(response).contracts[i].contractStatus;
					 /*$scope.listContracts = [];
					 $scope.listDetails = [];*/
					 $scope.listContracts[i]=JSON.parse(response).contracts[i].contractID; //error junk values 
					 $scope.listDetails[i]=JSON.parse(response).contracts[i];
					
				}
				//console.log(JSON.parse(response).contracts[i].contractID)
				console.log("data"+$scope.listContracts);				
				console.log("data"+$scope.listContractsStatus);

				if($scope.listContractsStatus == 'PAYMENT_DUE_FROM_IB_TO_EB'){
					//$rootScope.status_val = false;
				}

				$scope.$apply();	
				//alert("data"+$scope.listContracts);
			},
			error: function(data) {
				console.log("list is not coming");
				
			}
			});	 

///----------------view lc form to accept by exporter bank-----------------///


			$scope.view_lc_form=function(x,z){
				
				console.log(x);
				console.log($scope.listContracts.sort());

				//$rootScope.lc_form_index=x;
				$rootScope.contract_no=$scope.listContracts[x];
				//$location.path('/importer_form_view');
				console.log($rootScope.contract_no);


				status1=z;
                console.log(status1);
				console.log(x);
					
				if((localStorage.getItem('setrolename'))=="Importer")
                {     
                $rootScope.creat_ed=true;
                $rootScope.acc_btn=true;
                $rootScope.rej_btn=true;
                }
            else
                if((localStorage.getItem('setrolename'))=="Exporter")
                {

                $rootScope.creat_ed=true;
                $rootScope.acc_btn=true;
                $rootScope.rej_btn=true;
                }
            else
                if((localStorage.getItem('setrolename'))=="Insurance Company")
                {

                $rootScope.creat_ed=true;
                $rootScope.acc_btn=true;
                $rootScope.rej_btn=true;
                }
              else
                if((localStorage.getItem('setrolename'))=="Shipping Company")
                {

                $rootScope.creat_ed=true;
                $rootScope.acc_btn=true;
                $rootScope.rej_btn=true;
                }
				
                if(status1=="CREATED_LC_BY_IB")
                {
                	console.log(status1);
                    $rootScope.creat_ed=true;
                    $rootScope.acc_btn=false;
                    $rootScope.rej_btn=false;
                }
                else if(status1=="ACCEPTED_LC_BY_EB"){
                    console.log(status1);
                    $rootScope.creat_ed=false;
                    $rootScope.acc_btn=true;
                    $rootScope.rej_btn=true;
                    console.log($rootScope.creat_ed);
                    console.log($rootScope.acc_btn);
                    console.log($rootScope.rej_btn);
                }
                else if(status1=="SUBMITTED_ED_BY_EB"){
                    $rootScope.creat_ed=true;
                    $rootScope.acc_btn=true;
                    $rootScope.rej_btn=true;
                }
                else if(status1=="REJECTED_LC_BY_EB")
                    {
                        $rootScope.creat_ed=true;
                        $rootScope.acc_btn=true;
                        $rootScope.rej_btn=true;
                    }
                
                else if(status1=="ACCEPTED_ED_BY_IB")
                {
                $rootScope.creat_ed=true;
                $rootScope.acc_btn=true;
                $rootScope.rej_btn=true;
                }
                
 
					

			//alert($scope.listContracts);

			$.ajax({
			type: "GET",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=getLC&contractID="+$rootScope.contract_no,
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {

					
			$rootScope.imp_form_view=JSON.parse(response);

			console.log($rootScope.imp_form_view.Receiver);
			$location.path('/exporter_viewform');
			//if(!$scope.$$phase) {
			$scope.$apply();
			//}					
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}
			});			
			}

/////---------------- Accept LC form btn call ------------------//////////////

			$scope.lc_accept=function(x)
			{
				$rootScope.creat_ed=false;
	            $rootScope.rej_btn=true;
	            $rootScope.acc_btn=true;
			console.log("passing data"+x)
			///alert("clicked")
			$.ajax({
			type: "POST",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=acceptLC&contractID="+x,
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {
                        
			alert("LC Accepted");
            //$window.location.reload();

			},
			error: function() {
			alert("error");

			}
			});
            $scope.creat_ed2=true;
			}
			
		/////---------------- Reject LC form btn call ------------------//////////////

			$scope.lc_reject=function(x)
			{
				$rootScope.rej_btn=true;
	            $rootScope.acc_btn=true;
			console.log("passing data"+x)
			///alert("clicked")
			$.ajax({
			type: "POST",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=rejectLC&contractID="+x,
			async: true,

			contentType: "application/json",
			dataType: "text",
			success: function(response) {

			

			alert("LC Rejected");
			$location.path('/exporter');
			$scope.$apply();
			$window.location.reload();

			},
			error: function() {
			alert("error");

			}
			});
			 $scope.creat_ed2=true;
			}

///////---------------------On Accept the lc by eb move to next page to submit export docs  -------------/////
			$scope.create_ed=function(x)
			{
			/*$scope.upload_btn=false;*/
			$location.path('/exporter_doc');
			}

//--------- log out function calls -------//
			$scope.movetologinpage=function(){

			$.ajax({
			type: "POST",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=logout",

			crossDomain: true,
			async: true,
			//data: loginInfo4,
			contentType: "text/plain",
			dataType: "text",

			success: function(data,status, xhr) {
			console.log("logout successfully from Exporter bank")
			$location.path('/');
			$scope.$apply();
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			console.log("Error: in logout")
			}
			});

			}


    /////--------------- Submit export documents ------------------///////////




// submit export json data

	$scope.submit_ed1=function(x,y,z,form){

  		console.log($scope.getnofromcookie);

		var insurance = y;
		var shipping = z;

		//Invoice 
		$scope.formData1.PAYER =  $rootScope.form.PAYER;
		$scope.formData1.PAYEE =  $rootScope.form.PAYEE;
		$scope.formData1.TAX_REGISTRY_NO = $rootScope.form.TAX_REGISTRY_NO;
		$scope.formData1.INVOICE_CODE =  $rootScope.form.INVOICE_CODE;
		$scope.formData1.INVOICE_NUMBER = $rootScope.form.INVOICE_NUMBER;
		$scope.formData1.PRINTING_NO = $rootScope.form.PRINTING_NO;
		$scope.formData1.TOTAL_IN_WORDS = $rootScope.form.TOTAL_IN_WORDS;
		$scope.formData1.TOTAL_IN_FIGURES = $rootScope.form.TOTAL_IN_FIGURES;
		$scope.formData1.PRINT_NO = $rootScope.form.PRINT_NO;
		$scope.formData1.ANTI_FORGERY_CODE = $rootScope.form.ANTI_FORGERY_CODE;
		$scope.formData1.DATE_ISSUED =  $rootScope.form.DATE_ISSUED;
		$scope.formData1.DUE_DATE = $rootScope.form.DUE_DATE;
		$scope.formData1.SHIPPING_DATE =  $rootScope.form.SHIPPING_DATE;
		$scope.formData1.LC_NUMBER = $rootScope.form.LC_NUMBER;
		$scope.formData1.DATE_OF_PRESENTATION = $rootScope.form.DATE_OF_PRESENTATION;
		$scope.formData1.CURRENCY = $rootScope.form.CURRENCY; 

		//Bill of lading

		$scope.formData1.SCAC =  $rootScope.form.SCAC;
		$scope.formData1.BL_NO =  $rootScope.form.BL_NO;
		$scope.formData1.BOOKING_NO =  $rootScope.form.BOOKING_NO;
		$scope.formData1.EXPORT_REFERENCES = $rootScope.form.EXPORT_REFERENCES;
		$scope.formData1.SVC_CONTRACT = $rootScope.form.SVC_CONTRACT;
		$scope.formData1.ONWARD_INLAND_ROUTING = $rootScope.form.ONWARD_INLAND_ROUTING;
		$scope.formData1.SHIPPER_NAME_ADDRESS = $rootScope.form.SHIPPER_NAME_ADDRESS;
		$scope.formData1.CONSIGNEE_NAME_ADDRESS =  $rootScope.form.CONSIGNEE_NAME_ADDRESS;
		$scope.formData1.VESSEL = $rootScope.form.VESSEL;
		$scope.formData1.VOYAGE_NO = $rootScope.form.VOYAGE_NO;
		$scope.formData1.PORT_OF_LOADING =  $rootScope.form.PORT_OF_LOADING;
		$scope.formData1.PORT_OF_DISCHARGE = $rootScope.form.PORT_OF_DISCHARGE;
		$scope.formData1.PLACE_OF_RECEIPT = $rootScope.form.PLACE_OF_RECEIPT;
		$scope.formData1.PLACE_OF_DELIVERY =  $rootScope.form.PLACE_OF_DELIVERY;
		$scope.formData1.FREIGHT_AND_CHARGES =  $rootScope.form.FREIGHT_AND_CHARGES;
		$scope.formData1.RATE = $rootScope.form.RATE;
		$scope.formData1.UNIT =  $rootScope.form.UNIT;
		$scope.formData1.CURRENCY = $rootScope.form.CURRENCY;
		$scope.formData1.PREPAID =  $rootScope.form.PREPAID;
		$scope.formData1.TOTAL_CONTAINERS_RECEIVED_BY_CARRIER = $rootScope.form.TOTAL_CONTAINERS_RECEIVED_BY_CARRIER;
		$scope.formData1.CONTAINER_NUMBER =  $rootScope.form.CONTAINER_NUMBER;
		$scope.formData1.PLACE_OF_ISSUE_OF_BL =  $rootScope.form.PLACE_OF_ISSUE_OF_BL;
		$scope.formData1.NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS =  $rootScope.form.NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS;
		$scope.formData1.DATE_OF_ISSUE_OF_BL = $rootScope.form.DATE_OF_ISSUE_OF_BL;
		$scope.formData1.DECLARED_VALUE = $rootScope.form.DECLARED_VALUE;
		$scope.formData1.SHIPPER_ON_BOARD_DATE = $rootScope.form.SHIPPER_ON_BOARD_DATE;
		$scope.formData1.SIGNED_BY = $rootScope.form.SIGNED_BY;
		$scope.formData1.LC_NUMBER =  $rootScope.form.LC_NUMBER;
		$scope.formData1.DATE_OF_PRESENTATION = $rootScope.form.DATE_OF_PRESENTATION; 


		// Packaging List

		$scope.formData1.CONSIGNEE_NAME = $rootScope.form.CONSIGNEE_NAME;
		$scope.formData1.CONSIGNEE_ADDRESS = $rootScope.form.CONSIGNEE_ADDRESS;
		$scope.formData1.PACKING_LIST_NO = $rootScope.form.PACKING_LIST_NO;
		$scope.formData1.DATE = $rootScope.form.DATE;
		$scope.formData1.TOTAL_QUANTITY_MTONS =  $rootScope.form.TOTAL_QUANTITY_MTONS;
		$scope.formData1.TOTAL_NET_WEIGHT_KGS =  $rootScope.form.TOTAL_NET_WEIGHT_KGS;
		$scope.formData1.TOTAL_GROSS_WEIGHT_KGS =  $rootScope.form.TOTAL_GROSS_WEIGHT_KGS;
		$scope.formData1.DELIVERY_TERMS =  $rootScope.form.DELIVERY_TERMS;
		$scope.formData1.DOCUMENTARY_CREDIT_NUMBER = $rootScope.form.DOCUMENTARY_CREDIT_NUMBER;
		$scope.formData1.METHOD_OF_LOADING = $rootScope.form.METHOD_OF_LOADING;
		$scope.formData1.CONTAINER_NUMBER =  $rootScope.form.CONTAINER_NUMBER;
		$scope.formData1.PORT_OF_LOADING = $rootScope.form.PORT_OF_LOADING;
		$scope.formData1.PORT_OF_DISCHARGE = $rootScope.form.PORT_OF_DISCHARGE;
		$scope.formData1.DATE_OF_PRESENTATION = $rootScope.form.DATE_OF_PRESENTATION; 

		console.log($scope.formData1);

		console.log($scope.formData1.PAYER);
		console.log(insurance);
		console.log(shipping);
		
		if(($rootScope.form.PAYER == "") || ($rootScope.form.PAYEE == "") ||  ( $rootScope.form.TAX_REGISTRY_NO == "") ||
				($rootScope.form.INVOICE_CODE == "") ||($rootScope.form.INVOICE_NUMBER == "") ||($rootScope.form.PRINTING_NO   == "")||( $rootScope.form.TOTAL_IN_WORDS == "" )||
				($rootScope.form.TOTAL_IN_FIGURES == "" )||($rootScope.form.PRINT_NO == "" )||($rootScope.form.ANTI_FORGERY_CODE == "" )||($rootScope.form.DATE_ISSUED == "" ) ||
				( $rootScope.form.DUE_DATE == "") ||($rootScope.form.SHIPPING_DATE == "")||( $rootScope.form.LC_NUMBER == "")||($rootScope.form.DATE_OF_PRESENTATION == "")||
				($rootScope.form.CURRENCY == "")||($rootScope.form.SCAC == "" )||($rootScope.form.BL_NO == "" )||($rootScope.form.BOOKING_NO == "") ||
				($rootScope.form.EXPORT_REFERENCES == "" ) ||($rootScope.form.SVC_CONTRACT == "")||($rootScope.form.ONWARD_INLAND_ROUTING == "")||($rootScope.form.SHIPPER_NAME_ADDRESS == "") ||
				($rootScope.form.CONSIGNEE_NAME_ADDRESS == "") || ($rootScope.form.VESSEL == "") || ($rootScope.form.VOYAGE_NO == "") || ($rootScope.form.PORT_OF_LOADING == "")||
				($rootScope.form.PORT_OF_DISCHARGE == "") ||($rootScope.form.PLACE_OF_RECEIPT == "") ||($rootScope.form.PLACE_OF_DELIVERY   == "")||( $rootScope.form.FREIGHT_AND_CHARGES == "" )||
				($rootScope.form.RATE == "" )||($rootScope.form.UNIT == "" )||($rootScope.form.CURRENCY == "" )||($rootScope.form.PREPAID == "" ) ||
				( $rootScope.form.TOTAL_CONTAINERS_RECEIVED_BY_CARRIER == "") ||($rootScope.form.CONTAINER_NUMBER == "")||( $rootScope.form.PLACE_OF_ISSUE_OF_BL == "")||($rootScope.form.NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS == "")||
				($rootScope.form.CURRENCY == "")||($rootScope.form.SCAC == "" )||($rootScope.form.BL_NO == "" )||($rootScope.form.BOOKING_NO == "") ||
				($rootScope.form.DATE_OF_ISSUE_OF_BL == "" ) ||($rootScope.form.DECLARED_VALUE == "")||($rootScope.form.SHIPPER_ON_BOARD_DATE == "")||($rootScope.form.SIGNED_BY == "") ||
				($rootScope.form.LC_NUMBER == "") || ($rootScope.form.DATE_OF_PRESENTATION == "") || 
				($rootScope.form.CONSIGNEE_NAME == "") ||($rootScope.form.CONSIGNEE_ADDRESS == "") ||($rootScope.form.PACKING_LIST_NO   == "")||( $rootScope.form.DATE == "" )||
				($rootScope.form.TOTAL_QUANTITY_MTONS == "" )||($rootScope.form.TOTAL_NET_WEIGHT_KGS == "" )||($rootScope.form.TOTAL_GROSS_WEIGHT_KGS == "" )||($rootScope.form.DELIVERY_TERMS == "" ) ||
				($rootScope.form.TOTAL_CONTAINERS_RECEIVED_BY_CARRIER == "") ||($rootScope.form.CONTAINER_NUMBER == "")||( $rootScope.form.PLACE_OF_ISSUE_OF_BL == "")||($rootScope.form.NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS == "")||
				($rootScope.form.DOCUMENTARY_CREDIT_NUMBER == "")||($rootScope.form.METHOD_OF_LOADING == "" )||($rootScope.form.CONTAINER_NUMBER == "" )||($rootScope.form.PORT_OF_LOADING == "") ||
				($rootScope.form.PORT_OF_DISCHARGE == "" ) ||($rootScope.form.DATE_OF_PRESENTATION == "")||(insurance == 'undefined')||(shipping == 'undefined'))
				{ 
					alert("Please fill all the E/D Details");
				}  
				/*if(insurance==null || insurance =="" || shipping==null || shipping=="")
					{
						alert("Please fill Insurance and Shipping");
					}*/
				else{

	var Invoice_json={
			PAYER:$scope.formData1.PAYER,
			AYEE:$scope.formData1.PAYEE,
			TAX_REGISTRY_NO:Number($scope.formData1.TAX_REGISTRY_NO),
			INVOICE_CODE:Number($scope.formData1.INVOICE_CODE),
			INVOICE_NUMBER:Number($scope.formData1.INVOICE_NUMBER),
			PRINTING_NO:Number($scope.formData1.PRINTING_NO),
			Rows:[
			{SERVICE:"-",ITEM:2,AMOUNT_CHARGED:0,REMARKS:"-"},
			{SERVICE:"-",ITEM:21,AMOUNT_CHARGED:0,REMARKS:"-"}
			],
			TOTAL_IN_WORDS:$scope.formData1.TOTAL_IN_WORDS,
			TOTAL_IN_FIGURES:Number($scope.formData1.TOTAL_IN_FIGURES),
			PRINT_NO:Number($scope.formData1.PRINT_NO),
			ANTI_FORGERY_CODE:$scope.formData1.ANTI_FORGERY_CODE,
			DATE_ISSUED:$scope.formData1.DATE_ISSUED,
			DUE_DATE:$scope.formData1.DUE_DATE,
			SHIPPING_DATE:$scope.formData1.SHIPPING_DATE,
			LC_NUMBER:$scope.formData1.LC_NUMBER,
			DATE_OF_PRESENTATION:$scope.formData1.DATE_OF_PRESENTATION,
			CURRENCY:$scope.formData1.CURRENCY
			};
			
	var BillOfLading_json={	
			SCAC:$scope.formData1.SCAC,
			BL_NO:Number($scope.formData1.BL_NO),
			BOOKING_NO:Number($scope.formData1.BOOKING_NO),
			EXPORT_REFERENCES:$scope.formData1.EXPORT_REFERENCES,
			SVC_CONTRACT:$scope.formData1.SVC_CONTRACT,
			ONWARD_INLAND_ROUTING:$scope.formData1.ONWARD_INLAND_ROUTING,
			SHIPPER_NAME_ADDRESS:$scope.formData1.SHIPPER_NAME_ADDRESS,
			CONSIGNEE_NAME_ADDRESS:$scope.formData1.CONSIGNEE_NAME_ADDRESS,
			VESSEL:$scope.formData1.VESSEL,
			VOYAGE_NO:Number($scope.formData1.VOYAGE_NO),
			PORT_OF_LOADING:$scope.formData1.PORT_OF_LOADING,
			PORT_OF_DISCHARGE:$scope.formData1.PORT_OF_DISCHARGE,
			PLACE_OF_RECEIPT:$scope.formData1.PLACE_OF_RECEIPT,
			PLACE_OF_DELIVERY:$scope.formData1.PLACE_OF_DELIVERY,
			Rows:[
			{DESCRIPTION_OF_GOODS:"-",WEIGHT:0,MEASUREMENT:0}
			],
			FREIGHT_AND_CHARGES:Number($scope.formData1.FREIGHT_AND_CHARGES),
			RATE:Number($scope.formData1.RATE),
			UNIT:Number($scope.formData1.UNIT),
			CURRENCY:$scope.formData1.CURRENCY,
			PREPAID:$scope.formData1.PREPAID,
			TOTAL_CONTAINERS_RECEIVED_BY_CARRIER:Number($scope.formData1.TOTAL_CONTAINERS_RECEIVED_BY_CARRIER),
			CONTAINER_NUMBER:$scope.formData1.CONTAINER_NUMBER,
			PLACE_OF_ISSUE_OF_BL:$scope.formData1.PLACE_OF_ISSUE_OF_BL,
			NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS:$scope.formData1.NUMBER_AND_SEQUENCE_OF_ORIGINAL_BLS,
			DATE_OF_ISSUE_OF_BL:$scope.formData1.DATE_OF_ISSUE_OF_BL,
			DECLARED_VALUE:Number($scope.formData1.DECLARED_VALUE),
			SHIPPER_ON_BOARD_DATE:$scope.formData1.SHIPPER_ON_BOARD_DATE,
			SIGNED_BY:$scope.formData1.SIGNED_BY,
			LC_NUMBER:$scope.formData1.LC_NUMBER,
			DATE_OF_PRESENTATION:$scope.formData1.DATE_OF_PRESENTATION
			};

	var PackagingList_json={
			CONSIGNEE_NAME:$scope.formData1.CONSIGNEE_NAME,
			CONSIGNEE_ADDRESS:$scope.formData1.CONSIGNEE_ADDRESS,
			PACKING_LIST_NO:$scope.formData1.PACKING_LIST_NO,
			DATE:$scope.formData1.DATE,
			Rows:[
			{DESCRIPTION_OF_GOODS:"-",QUANTITY_MTONS:0,NET_WEIGHT_KGS:0,GROSS_WEIGHT_KGS:0}
			],
			TOTAL_QUANTITY_MTONS:Number($scope.formData1.TOTAL_QUANTITY_MTONS),
			TOTAL_NET_WEIGHT_KGS:Number($scope.formData1.TOTAL_NET_WEIGHT_KGS),
			TOTAL_GROSS_WEIGHT_KGS:Number($scope.formData1.TOTAL_GROSS_WEIGHT_KGS),
			DELIVERY_TERMS:$scope.formData1.DELIVERY_TERMS ,
			DOCUMENTARY_CREDIT_NUMBER:$scope.formData1.DOCUMENTARY_CREDIT_NUMBER ,
			METHOD_OF_LOADING:$scope.formData1.METHOD_OF_LOADING,
			CONTAINER_NUMBER:$scope.formData1.CONTAINER_NUMBER,
			PORT_OF_LOADING:$scope.formData1.PORT_OF_LOADING,
			PORT_OF_LOADING:$scope.formData1.PORT_OF_LOADING,
			DATE_OF_PRESENTATION:$scope.formData1.DATE_OF_PRESENTATION
			};

	var temp_obj={
			Invoice: Invoice_json,
			InvoicePDF: "",
			BillOfLading: BillOfLading_json,
			BillOfLadingPDF: "",
			PackagingList:PackagingList_json,
			PackagingListPDF: "",
			InsuranceConpany: insurance,
			ShippingCompany: shipping
			} 

	console.log(temp_obj);

			$.ajax({
			type: "POST",
			url: "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=submitED&contractID="+$scope.getnofromcookie ,
			async: true,
			data: JSON.stringify(temp_obj),

			contentType: "application/json",
			dataType: "text",
			success: function(response) {
			console.log(response);
			alert("Export documents submitted successfully");
			$location.path('/exporter'); 
			$scope.$apply();
			},
			error: function(jqXHR, textStatus, errorThwrown) {
			alert("error");
			}

			}); 
		}
	}
	
// Submit ED_JSON ends here
});



//export cntrl ends //


	//------------3.Exporter cntrl Controller--------//



app.controller('TabController',function($scope,$location,$rootScope,SharedDatacid,SharedDataService,ContractsList,edlist,statusarray,$window,$http){
	console.log("Tabcontroller active");

	 $scope.tab = 1;

    $scope.setTab = function(newTab){
      $scope.tab = newTab;
    };

    $scope.isSet = function(tabNum){
      return $scope.tab === tabNum;
    };
});
