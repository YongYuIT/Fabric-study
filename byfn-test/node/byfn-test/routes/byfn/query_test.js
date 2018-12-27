var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function (req, res, next) {

    //use network byfn, ref to https://www.cnblogs.com/studyzy/p/7524245.html

    var hfc = require('fabric-client')
    var path = require('path')
    var fs = require('fs')

    var options = {
        user_id: 'Admin@org1.example.com',
        user_key_path: '/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/keystore',
        user_pem: '/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem',
        msp_id: 'Org1MSP',
        peer0_org1_name: 'peer0.org1.example.com',
        peer0_org1_url: 'grpcs://localhost:7051',//check env $CORE_PEER_TLS_ENABLED on peer
        peer0_org1_ca: '/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt',
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
        channel = client.newChannel(options.channel_id)

        /*
        peer_ca = fs.readFileSync(options.peer0_org1_ca)
        console.log("apem-->" + Buffer.from(peer_ca).toString())
        peer = client.newPeer(options.peer0_org1_url, {
            pem: Buffer.from(peer_ca).toString(),
            'ssl-target-name-override': options.peer0_org1_name
        })
        */


        //https://www.lijiaocn.com/%E9%97%AE%E9%A2%98/2018/07/15/hyperledger-fabric-nodejs-problem.html
        var peer = client.newPeer(
            options.peer0_org1_url,
            {
                pem: fs.readFileSync('/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/ca.crt', {encoding: 'utf8'}),
                clientKey: fs.readFileSync('/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/client.key', {encoding: 'utf8'}),
                clientCert: fs.readFileSync('/home/yong/fabric-samples/first-network/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/client.crt', {encoding: 'utf8'}),
                'ssl-target-name-override': options.peer0_org1_name
            }
        );

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