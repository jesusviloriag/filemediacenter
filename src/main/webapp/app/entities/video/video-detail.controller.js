(function() {
    'use strict';

    angular
        .module('mediaCenterApp')
        .controller('VideoDetailController', VideoDetailController);

    VideoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Video'];

    function VideoDetailController($scope, $rootScope, $stateParams, entity, Video) {
        var vm = this;

        vm.video = entity;

        var unsubscribe = $rootScope.$on('mediaCenterApp:videoUpdate', function(event, result) {
            vm.video = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
