app.controller("payController", function ($scope, $location,cartService,payService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //生成支付二维码
    $scope.createNative = function () {
        //支付业务id
        $scope.outTradeNo = $location.search()["outTradeNo"];
        payService.createNative($scope.outTradeNo).success(function (response) {
            if("SUCCESS"==response.result_code){//创建支付地址成功
                //计算总金额
                $scope.money=(response.totalFee/100).toFixed(2);
                //生成支付地址的二维码
                var qr = new QRious({
                    element: document.getElementById("qrious"),
                    size:250,
                    level:"M",
                    value:response.code_url
               });

                //查询支付状态
                $scope.queryPayStatus($scope.outTradeNo);
            }else{
                alert("生成二维码失败！");
            }

        });
    };

    //查询支付状态
    $scope.queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if (response.success) {
                location.href = "paysuccess.html#?money=" + $scope.money;
            }else{
                if (response.message=="支付超时"){
                    //重新生成二维码
                    alert("支付超时，请重新生成二维码");
                    $scope.createNative();
                }else {
                    //支付失败页面
                    location.href = "payfail.html";
                }
            }
        });
    };

    //获取总金额
    $scope.getMoney = function () {
        $scope.money = $location.search()["money"];
    };

});