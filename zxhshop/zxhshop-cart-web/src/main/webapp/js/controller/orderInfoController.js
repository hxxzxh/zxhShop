app.controller("orderInfoController", function ($scope, cartService,addressService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    $scope.findCartList = function () {
        cartService.findCartList().success(function (respones) {
            $scope.cartList = respones;
            //计算购买总数和总价格
            $scope.totalValue = cartService.sumTotalValue(respones);
        });
    };

    $scope.findAddressList = function () {
        addressService.findAddressList().success(function (respones) {
            $scope.addressList = respones;
            for (var i = 0; i < $scope.addressList.length; i++) {
                var address = $scope.addressList[i];
                if(address.isDefault=="1") {
                    $scope.address = address;
                    break;
                }
            }
        });
    };

    $scope.isAddressSelected = function (address) {
        return $scope.address==address;
    };

    $scope.selectAddress = function (address) {
        $scope.address=address;
    };

    //订单
    $scope.order = {"paymentType":"1"};

    $scope.selectPaymentType = function (type) {
        $scope.order.paymentType = type;
    };

    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                if ($scope.order.paymentType=="1"){
                    //携带支付业务id，跳转到支付页面
                    location.href = "pay.html#?outTradeNo=" + response.message;
                }else{
                    location.href = "paysuccess.html";
                }
            }else{
                alert(response.message);
            }
        });
    };
});













