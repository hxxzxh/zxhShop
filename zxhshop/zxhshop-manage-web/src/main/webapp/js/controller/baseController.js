app.controller("baseController",function ($scope) {
    //初始化分页参数
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,//页大小
        perPageOptions: [10, 20, 30, 40, 50],//可选择的每页大小
        onChange: function () {//当上述的参数发生变化了后就触发
            $scope.reloadList();
        }
    };

    //加载表格数据
    $scope.reloadList = function () {
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //定义一个放置选择了id的数组
    $scope.selectedIds = [];

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            var index = $scope.selectedIds.indexOf(id);
            //删除位置，删除个数
            $scope.selectedIds.splice(index, 1);
        }
    };

    //将一个json集合字符串中的每一个对象对应的某个属性的值拼接后返回
    $scope.jsonToString = function (jsonListStr, key) {
        var str = "";
        //将json集合字符串转为Json对象
        var jsonArray = JSON.parse(jsonListStr);
        for (var i = 0; i < jsonArray.length; i++) {
            var obj = jsonArray[i];
            if(str.length > 0){
                str += "," + obj[key];
            } else {
                str = obj[key];
            }
        }

        return str;
    };


})