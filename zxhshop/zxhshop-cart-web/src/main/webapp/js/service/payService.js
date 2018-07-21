app.service("payService", function ($http) {
    this.createNative = function (outTradeNo) {
        return $http.get("pay/createNative.do?outTradeNo="+outTradeNo+"&r="+Math.random());
    };

    //查询支付状态
    this.queryPayStatus = function (outTradeNo) {
        return $http.get("pay/queryPayStatus.do?outTradeNo="+outTradeNo+"&r="+Math.random());
    };
});