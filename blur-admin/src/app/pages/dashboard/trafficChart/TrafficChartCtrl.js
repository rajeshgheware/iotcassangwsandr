/**
 * @author v.lugovksy created on 16.12.2015
 */
(function() {
	'use strict';

	angular.module('BlurAdmin.pages.dashboard').controller('TrafficChartCtrl',
			TrafficChartCtrl);

	/** @ngInject */
	function TrafficChartCtrl($timeout, $http, $rootScope, $scope, baConfig,
			colorHelper) {

		$scope.totalevents = [];
		$scope.totalSensorEvents = 0;
		$scope.sensor_one = '';
		$scope.sensor_one_label = 'Accelerometer';
		$scope.sensor_two = '';
		$scope.sensor_two_label = 'Gyroscope';
		$scope.sensor_three = '';
		$scope.sensor_three_label = 'Gyroscope U';
		$scope.sensor_four = '';
		$scope.sensor_four_label = 'Magnetometer';
		$scope.sensor_five = '';
		$scope.sensor_five_label = 'Magnetometer U';
		$scope.sensor_six = '';
		$scope.sensor_six_label = 'NMEA';
		$scope.sensor_seven = '';
		$scope.sensor_seven_label = 'GPS';
		

		var stompClient = null;
		var socket = new SockJS('/sensor-websocket');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function(frame) {
			console.log('Connected: ' + frame);
			stompClient.subscribe('/topic/sensor-events', function(data) {
				$rootScope.$apply(function() {
					var sensorEvents = JSON.parse(data.body);
					var totalSensorEvents = 0;
					for (var i = 0; i < sensorEvents.length; i++) {
						totalSensorEvents += sensorEvents[i].events_count;
					}
					$scope.totalSensorEvents = totalSensorEvents;

					$scope.totalevents = sensorEvents;
					$scope.sensor_one = sensorEvents[0].events_count;
					$scope.sensor_one_label = sensorEvents[0].sensor;
					$scope.doughnutData[0].value = sensorEvents[0].events_count;
					$scope.doughnutData[0].label = sensorEvents[0].sensor;
					$scope.sensor_two = sensorEvents[1].events_count;
					$scope.sensor_two_label = sensorEvents[1].sensor;
					$scope.doughnutData[1].value = sensorEvents[1].events_count ;
					$scope.doughnutData[1].label = sensorEvents[1].sensor;
					$scope.sensor_three = sensorEvents[2].events_count;
					$scope.sensor_three_label = sensorEvents[2].sensor;
					$scope.doughnutData[2].value = sensorEvents[2].events_count;
					$scope.doughnutData[2].label = sensorEvents[2].sensor;
					$scope.sensor_four = sensorEvents[3].events_count;
					$scope.sensor_four_label = sensorEvents[3].sensor;
					$scope.doughnutData[3].value = sensorEvents[3].events_count ;
					$scope.doughnutData[3].label = sensorEvents[3].sensor;
					$scope.sensor_five = sensorEvents[4].events_count;
					$scope.sensor_five_label = sensorEvents[4].sensor;
					$scope.doughnutData[4].value = sensorEvents[4].events_count;
					$scope.doughnutData[4].label = sensorEvents[4].sensor;
					$scope.sensor_six = sensorEvents[5].events_count;
					$scope.sensor_six_label = sensorEvents[5].sensor;
					$scope.doughnutData[5].value = sensorEvents[5].events_count;
					$scope.doughnutData[5].label = sensorEvents[5].sensor;
					$scope.sensor_seven = sensorEvents[6].events_count;
					$scope.sensor_seven_label = sensorEvents[6].sensor;				
					$scope.doughnutData[6].value = sensorEvents[6].events_count;
					$scope.doughnutData[6].label = sensorEvents[6].sensor;
				});
			});
		});

		$scope.transparent = baConfig.theme.blur;
		var dashboardColors = baConfig.colors.dashboard;
		$scope.doughnutData = [ {
			value : $scope.sensor_one,
			color : dashboardColors.white,
			highlight : colorHelper.shade(dashboardColors.white, 15),
			label : $scope.sensor_one_label,
			percentage : 100,
			order : 0,
		}, {
			value : $scope.sensor_two,
			color : dashboardColors.blueStone,
			highlight : colorHelper.shade(dashboardColors.blueStone, 15),
			label : $scope.sensor_two_label,
			percentage : 100,
			order : 1,
		},{
			value : $scope.sensor_three,
			color : dashboardColors.blueStone,
			highlight : colorHelper.shade(dashboardColors.blueStone, 15),
			label : $scope.sensor_three_label,
			percentage : 100,
			order : 1,
		}, {
			value : $scope.sensor_four,
			color : dashboardColors.silverTree,
			highlight : colorHelper.shade(dashboardColors.silverTree, 15),
			label : $scope.sensor_four_label,
			percentage : 15,
			order : 2,
		},{
			value : $scope.sensor_five,
			color : dashboardColors.silverTree,
			highlight : colorHelper.shade(dashboardColors.silverTree, 15),
			label : $scope.sensor_five_label,
			percentage : 15,
			order : 2,
		},  {
			value : $scope.sensor_six,
			color : dashboardColors.gossip,
			highlight : colorHelper.shade(dashboardColors.gossip, 15),
			label : $scope.sensor_six_label,
			percentage : 15,
			order : 3,
		}, {
			value : $scope.sensor_seven,
			color : dashboardColors.gossip,
			highlight : colorHelper.shade(dashboardColors.gossip, 15),
			label : $scope.sensor_seven_label,
			percentage : 15,
			order : 4,
		}];

		function loadDoughnut() {
			var ctx = document.getElementById('chart-area').getContext('2d');
			window.myDoughnut = new Chart(ctx).Doughnut($scope.doughnutData, {
				segmentShowStroke : false,
				percentageInnerCutout : 64,
				responsive : true
			});
		}
		function initialDashboardData() {
			$http.get('/rest/sensor-events').then(function(response) {
				var sensorEvents = response.data;
				var totalSensorEvents = 0;
				for (var i = 0; i < sensorEvents.length; i++) {
					totalSensorEvents += sensorEvents[i].events_count;
				}
				$scope.totalSensorEvents = totalSensorEvents;
				$scope.totalevents = sensorEvents;
			});
		}

		$timeout(function() {
			loadDoughnut();
			initialDashboardData();
		}, 1000);
	}
})();