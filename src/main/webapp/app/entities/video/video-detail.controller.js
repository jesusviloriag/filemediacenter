(function() {
    'use strict';

    angular
        .module('mediaCenterApp')
        .controller('VideoDetailController', VideoDetailController);

    VideoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Video', 'video'];

    function VideoDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Video, video) {
        var vm = this;

        vm.video = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        video.addSource('mp4', vm.video.direccionEnServidor);

        var unsubscribe = $rootScope.$on('mediaCenterApp:videoUpdate', function(event, result) {
            vm.video = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
