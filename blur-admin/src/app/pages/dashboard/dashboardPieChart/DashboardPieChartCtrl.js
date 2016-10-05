/**
 * @author v.lugovksy
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.dashboard')
      .controller('DashboardPieChartCtrl', DashboardPieChartCtrl);
  
  /** @ngInject */
  function DashboardPieChartCtrl($rootScope, $http, $scope, $timeout, baConfig, baUtil) {

	  $scope.devicestotal = '';
	  $scope.devicesonline = '';
	  $scope.usersonline = '';
	  var stompClient = null;
	  var socket = new SockJS('/sensor-websocket');
	  stompClient = Stomp.over(socket);
	  stompClient.connect({}, function (frame) {
	        console.log('Connected: ' + frame);
	        stompClient.subscribe('/topic/devices-total', function (devices) {
	            $rootScope.$apply(function(){
		            $scope.devicestotal = devices.body;
		            $scope.charts[0].stats = devices.body;
				  });	            
	        });
	        stompClient.subscribe('/topic/users-online', function (users) {
	            $rootScope.$apply(function(){
		            $scope.usersonline = users.body;
		            $scope.charts[2].stats=users.body;
				  });
	        });
	        stompClient.subscribe('/topic/devices-online', function (devicesonline) {
	            $rootScope.$apply(function(){
	            	$scope.devicesonline = devicesonline.body;
		            $scope.charts[1].stats = devicesonline.body;
				  });
	            
	        });
	        stompClient.send("/app/i-am-alive", {}, JSON.stringify({name: 'browserclient',message:'I am alive'}));
	  });
	    
	    
    var pieColor = baUtil.hexToRGB(baConfig.colors.defaultText, 0.2);
    $scope.charts = [{
      color: pieColor,
      description: 'Devices Total',
      stats: $scope.devicestotal,
      icon: 'fa fa-android fa-4x',
    }, {
      color: pieColor,
      description: 'Devices Online',
      stats: $scope.devicesonline,
      icon: 'fa fa-android fa-4x',
    }, {
      color: pieColor,
      description: 'Users Online',
      stats: $scope.usersonline,
      icon: 'fa fa-users fa-3x',
    }/*, {
      color: pieColor,
      description: 'Returned',
      stats: '32,592',
      icon: 'refresh',
    }*/
    ];

    function getRandomArbitrary(min, max) {
      return Math.random() * (max - min) + min;
    }

    function loadPieCharts() {
      $('.chart').each(function () {
        var chart = $(this);
        chart.easyPieChart({
          easing: 'easeOutBounce',
          onStep: function (from, to, percent) {
            $(this.el).find('.percent').text(Math.round(percent));
          },
          barColor: chart.attr('rel'),
          trackColor: 'rgba(0,0,0,0)',
          size: 84,
          scaleLength: 0,
          animation: 2000,
          lineWidth: 9,
          lineCap: 'round',
        });
      });

      $('.refresh-data').on('click', function () {
        updatePieCharts();
      });
    }

    function updatePieCharts() {
      $('.pie-charts .chart').each(function(index, chart) {
        $(chart).data('easyPieChart').update(getRandomArbitrary(55, 90));
      });
    }
    
    function initialDashboardData(){
    	$http.get('/rest/devices-total').
        then(function(response) {
        	$scope.devicestotal = response.data;
        });
    	$http.get('/rest/users-online').
        then(function(response) {
        	$scope.usersonline = response.data;
        });
    	$http.get('/rest/devices-online').
        then(function(response) {
        	$scope.devicesonline = response.data;
        });
    }

    $timeout(function () {
      initialDashboardData();
      loadPieCharts();
      updatePieCharts();
    }, 1000);
  }
})();