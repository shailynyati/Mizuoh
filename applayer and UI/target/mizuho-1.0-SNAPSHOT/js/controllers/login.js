//------------login cntrl--------//

app.controller('loginCtrl', function($scope,$location,$rootScope,ContractsList,edlist,statusarray,$window,$timeout,$http){

    localStorage.setItem('setrolename',"");
    console.log(localStorage.setItem('setrolename',""));
    
    $scope.submitlogin=function(a,b,c){
        
      //  $location.path('/success');

        var encodedString1 = encodeURIComponent($scope.username);
        var encodedString2 = encodeURIComponent($scope.password);
        var encodedString3 = encodeURIComponent($scope.role);
                            
                            

        var loginInfo = "userID=" + encodedString1 + "\npasscode=" + encodedString2 + "\nuserRole=" + encodedString3;

        var loginURL = "https://mizuhobcdev.mybluemix.net/mizuho/TransactionManager?method=login";
        


        $.ajax({
        type: "POST",
        url: loginURL,

        crossDomain: true,
        async: true,
        data: loginInfo,
        contentType: "text/plain",

        success: function(response,status, xhr) {
        /*console.log(response);
        console.log(JSON.parse(response).RoleName);
        console.log(JSON.parse(response).Status);
        console.log(JSON.parse(response).RoleName);*/

            localStorage.setItem('setrolename', JSON.parse(response).RoleName);
            localStorage.setItem('setroleid', JSON.parse(response).RoleID);
            localStorage.setItem('setusername', JSON.parse(response).UserID);

        if(!$scope.$$phase) {
        $scope.$apply();
        }

        console.log("encodedString3:"+encodedString3);
        console.log("encodedString2:"+encodedString2);
        console.log("encodedString1:"+encodedString1);
            
         // if(((JSON.parse(response).Status) == "OK" )&& (encodedString3!=null||enocodedString3!="")){
            
            if(encodedString3== 'undefined')
                {
                    console.log("its null");
                }
        
        if(encodedString3!='undefined'||encodedString2!='undefined'||encodedString1!='undefined')
            {
                
         if((JSON.parse(response).Status) == "OK" ) {
    
                console.log("coming here");
        if(encodedString3=="ImporterBank"){
        if(JSON.parse(response).RoleName== "Importer Bank"){
        console.log("logged in as importer bank");
        $location.path('/success');
        $scope.$apply();
        }
        else{
            alert("Please enter valid credentials !!")
        }
         }
         else if(encodedString3=="ExporterBank"){
        if(JSON.parse(response).RoleName== "Exporter Bank"){
        console.log("logged in as exporter bank");
        $location.path('/exporter');
        $scope.$apply();
        }
        else{
            alert("Please enter valid credentials !!")
        }
         }
         else if(encodedString3 == "Importer"){
        if(JSON.parse(response).RoleName== "Importer"){
        console.log("logged in as Importer");
        $location.path('/importer_role');
        $scope.$apply();
        }
        else{
            alert("Please enter valid credentials !!")
        }
         }

         else if(encodedString3 == "Exporter"){
        if(JSON.parse(response).RoleName== "Exporter"){
        console.log("logged in as Exporter");
        $location.path('/exporter_role');
        $scope.$apply();
        }   
        else{
            alert("Please enter valid credentials !!")
        }
         }


        else if(encodedString3 == "ShippingCompany"){
        if(JSON.parse(response).RoleName== "Shipping Company"){
        console.log("logged in as Shipping Company");
        $location.path('/shipping_company');
        $scope.$apply();
        }
        else{
            alert("Please enter valid credentials !!")
        }
       }


        else if(encodedString3 == "InsuranceCompany"){
        if(JSON.parse(response).RoleName== "Insurance Company"){
        console.log("logged in as Insurance Company");
        $location.path('/insurance_company');
        $scope.$apply();
        }
        else{
            alert("Please enter valid credentials !!")
        }
         }
        else if(encodedString3!=null||encodedString3!=""){
                alert("Please select a Role ")
        }
          
        }// 2nd close
            else{
                console.log("2nd close else");
            alert("Please enter valid credentials !!")
        }
        
        } // 1st close
        else{
            console.log("alert");
            alert("Please select a Role !!")
        }
        },
        error: function(jqXHR, textStatus, errorThwrown) {
            alert("Please select a Role !!")
            console.log("error");
        }
        });


    }
});


