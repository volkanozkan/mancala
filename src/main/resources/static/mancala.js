let websocket = null;
const app = angular.module('mancalaGame', []);
app.controller('mancalaController', function($scope, $window) {

    $scope.turnMessage = 'Pending Opponent';

    const username = $window.prompt("Please enter username", "");

    websocket = new WebSocket('ws://' + window.location.host +
        '/ws?' + username);

    websocket.onclose = function(event) {
        $window.alert("Game ended, refresh the page to play again!");
    };

    websocket.onmessage = function(event) {
        const msg = JSON.parse(event.data);

        $scope.myPits = msg.myPits;
        $scope.myBigPit = msg.myBigPit;
        $scope.opponentBigPit = msg.opponentBigPit;
        $scope.opponentPits = msg.opponentPits;
        $scope.myName = msg.myName;
        $scope.opponentName = msg.opponentName;
        $scope.turnBoolen = msg.myTurn;
        $scope.turnMessage = $scope.turnBoolen ? 'Its Your Turn ' +
            $scope.myName : 'Wait for your turn ' + $scope.myName;
        $scope.gameStatus = msg.gameStatus.description;
        $scope.gameOver = msg.gameOver;
        $scope.error = msg.error;
        
        if($scope.error !== null && $scope.error !== ''){
        	$window.alert($scope.error);
        	$scope.$apply();
            return;
        }
        
        if ($scope.gameOver) {
            if ($scope.gameStatus.includes('Opponent Left')) {
                $window.alert("Opponent Left, refresh the page to play again!");
                $scope.$apply();
                return;
            }

            let winner;
            if ($scope.myBigPit > $scope.opponentBigPit) {
                winner = $scope.myName + ' Won';
            } else if ($scope.myBigPit < $scope.opponentBigPit) {
                winner = $scope.opponentName + ' Won';
            } else {
                winner = 'Draw'
            }
            $scope.gameStatus = $scope.gameStatus + '(' +
                winner + ')';
            
            $window.alert("Game is over, " + winner +
                " Refresh the page to play again!");
        }

        $scope.$apply();
    };

    $scope.move = function(selectedPit) {
        if ($scope.gameOver) {
            $window.alert('Game is over.');
            return;
        }

        if (!$scope.turnBoolen) {
            $window.alert('Wait for your turn');
            return;
        }

        const data = {
            selectedPit: selectedPit
        };

        const stones = $scope.myPits[selectedPit];
        if (stones > 0) {
            sendMessage(websocket, data);
        } else {
            $window.alert('No stones on selected pit, choose different pit to play');
        }
    }

});

app.directive('animateOnChange', function($timeout) {
    return function(scope, element, attr) {
        scope.$watch(attr.animateOnChange, function(nv, ov) {
            if (nv !== ov) {
                var c;

                if (nv > ov) {
                    c = 'changed-increased';
                } else if (nv < ov) {
                    c = 'changed-decreased';
                }

                element.addClass(c);
                $timeout(function() {
                    element.removeClass(c);
                }, 1000);
            }
        });
    };
});

function sendMessage(websocket, message) {
    if (websocket.readyState === WebSocket.OPEN) {
        websocket.send(JSON.stringify(message));
    }
}
