(function() {
    'use strict';
    angular
        .module('mediaCenterApp')
        .factory('VideoFile', VideoFile);

    VideoFile.$inject = ['$resource'];

    function VideoFile ($resource) {
        var resourceUrl =  'api/videos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
