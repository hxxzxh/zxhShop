app.controller("cartController", function ($scope, cartService) {
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

    $scope.addItemToCartList = function (itemId,num) {
        cartService.addItemToCartList(itemId, num).success(function (respones) {
            if(respones.success){
                $scope.findCartList();
            }else{
                alert(respones.message);
            }
        });
    };

});