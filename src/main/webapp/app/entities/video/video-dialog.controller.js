(function() {
    'use strict';

    angular
        .module('mediaCenterApp')
        .controller('VideoDialogController', VideoDialogController);

    VideoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Video'];

    function VideoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Video) {
        var vm = this;

        vm.video = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        vm.video.direccionEnServidor = "- lleno luego -";

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.video.id !== null) {
                Video.update(vm.video, onSaveSuccess, onSaveError);
            } else {
                Video.save(vm.video, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('mediaCenterApp:videoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setArchivo = function ($file, video) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        video.nombreArchivo = $file.name;
                        video.archivo = base64Data;
                        video.archivoContentType = $file.type;
                    });
                });
            }
        };

    }
})();
