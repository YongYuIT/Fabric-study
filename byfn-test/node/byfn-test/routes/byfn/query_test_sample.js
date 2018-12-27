var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function (req, res, next) {

    //use network byfn, ref to https://www.cnblogs.com/studyzy/p/7524245.html
    //disable byfn tls
    //docker-compose-cli.yaml       CORE_PEER_TLS_ENABLED
    //base/docker-compose-base.yaml ORDERER_GENERAL_TLS_ENABLED
    //base/peer-base.yaml           CORE_PEER_TLS_ENABLED

    var hfc = require('fabric-client')
    var path = require('path')
    var fs = require('fs')

    var options = {
        user_id: 'Admin@org1.example.com',
        user_key_path: '/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore',
        user_pem: '/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem',
        msp_id: 'Org1MSP',
        peer0_org1_name: 'peer0.org1.example.com',
        peer0_org1_url: 'grpc://localhost:7051',
        channel_id: 'mychannel',
        chaincode_id: 'mycc',
    }


    var channel = {}
    var client = null

    getKeyFileInDir = function (dir) {
        var files = fs.readdirSync(dir)
        var keyFiles = []
        files.forEach(function (file_name) {
            var filePath = path.join(dir, file_name)
            if (file_name.endsWith('_sk')) {
                keyFiles.push(filePath)
            }
        })
        return keyFiles
    }

    client = new hfc()
    var user_info = {
        username: options.user_id,
        mspid: options.msp_id,
        cryptoContent: {
            privateKey: getKeyFileInDir(options.user_key_path)[0],
            signedCert: options.user_pem
        }
    }
    var store_path = path.join(__dirname, 'hfc-key-store');
    hfc.newDefaultKeyValueStore({path: store_path}).then(function (store) {
        client.setStateStore(store)
        return client.createUser(user_info)
    }).then(function (user) {
        console.log(user == null ? "null" : user.isEnrolled())
        channel = client.newChannel(options.channel_id)
        peer = client.newPeer(options.peer0_org1_url)
        channel.addPeer(peer)
    }).then(function () {
        var transaction_id = client.newTransactionID()
        var request = {
            chaincodeId: options.chaincode_id,
            txId: transaction_id,
            fcn: 'query',
            args: ['a']
        }
        return channel.queryByChaincode(request)
    }).then(function (responses) {
        console.log("response is -->", responses[0].toString())
    })

    res.send('passed!')

});

module.exports = router;