var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function (req, res, next) {

    function test_promise(test_value) {
        var my_promise = new Promise(function (_resolve, _reject) {
            console("this is fuck promise body");
            if (test_value == 1) {
                console("call _resolve");
                _resolve(test_value);
            } else {
                console("call _reject");
                _reject(test_value);
            }
        })
        return my_promise;
    }

    var test = 1
    test_promise(test).then(function (value) {
        console.log("this is fuck resolve body");
    }).catch(function (reason) {
        console.log("this is fuck reject body");
    });

    res.send('Hello World!')
});

module.exports = router;
