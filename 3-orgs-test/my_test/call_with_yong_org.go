package my_test

import (
	"fmt"
	"github.com/hyperledger/fabric-sdk-go/pkg/client/channel"
	mspclient "github.com/hyperledger/fabric-sdk-go/pkg/client/msp"
	"github.com/hyperledger/fabric-sdk-go/pkg/core/config"
	"github.com/hyperledger/fabric-sdk-go/pkg/fabsdk"
	"log"
)

func Call_with_yong() {
	//读取配置文件，创建SDK
	configProvider := config.FromFile("/mnt/hgfs/3-orgs-test/my_test/config-yong.yaml")
	sdk, err := fabsdk.New(configProvider)
	if err != nil {
		log.Fatalf("create sdk fail: %s\n", err.Error())
	}

	//读取配置文件(config.yaml)中的组织(yong.thinking.com)的用户(Admin)
	mspClient, err := mspclient.New(sdk.Context(),
		mspclient.WithOrg("yong.thinking.com"))
	if err != nil {
		log.Fatalf("create msp client fail: %s\n", err.Error())
	}

	adminIdentity, err := mspClient.GetSigningIdentity("Admin")
	if err != nil {
		log.Fatalf("get admin identify fail: %s\n", err.Error())
	} else {
		fmt.Println("AdminIdentify is found:")
		fmt.Println(adminIdentity)
	}

	//读取配置文件(config.yaml)中的通道(channels)的当前通道(mythnkingchannel)
	channelProvider := sdk.ChannelContext("mythnkingchannel",
		fabsdk.WithUser("Admin"),
		fabsdk.WithOrg("yong.thinking.com"))
	channelClient, err := channel.New(channelProvider)
	if err != nil {
		log.Fatalf("create channel client fail: %s\n", err.Error())
	}

	//调用链码
	var args [][]byte
	args = append(args, []byte("this is the fuck my_test for 3 orgs 20180824002"))
	request := channel.Request{
		ChaincodeID: "test_fuck_20180824004",
		Fcn:         "set_hello",
		Args:        args,
	}
	response, err := channelClient.Execute(request)
	if err != nil {
		log.Fatal("query fail: ", err.Error())
	} else {
		fmt.Printf("response is %s\n", response.Payload)
	}

	//调用链码
	request = channel.Request{
		ChaincodeID: "test_fuck_20180824004",
		Fcn:         "get_hello",
		Args:        nil,
	}
	response, err = channelClient.Query(request)
	if err != nil {
		log.Fatal("query fail: ", err.Error())
	} else {
		fmt.Printf("response is %s\n", response.Payload)
	}
}
