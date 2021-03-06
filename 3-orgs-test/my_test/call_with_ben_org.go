package my_test

import (
	"fmt"
	"github.com/hyperledger/fabric-sdk-go/pkg/client/channel"
	mspclient "github.com/hyperledger/fabric-sdk-go/pkg/client/msp"
	"github.com/hyperledger/fabric-sdk-go/pkg/core/config"
	"github.com/hyperledger/fabric-sdk-go/pkg/fabsdk"
	"log"
)

func Call_with_ben() {
	//读取配置文件，创建SDK
	configProvider := config.FromFile("/mnt/hgfs/3-orgs-test/my_test/config-ben.yaml")
	sdk, err := fabsdk.New(configProvider)
	if err != nil {
		log.Fatalf("create sdk fail: %s\n", err.Error())
	}

	//读取配置文件(config.yaml)中的组织(ben.thinking.com)的用户(Admin)
	mspClient, err := mspclient.New(sdk.Context(),
		mspclient.WithOrg("ben.thinking.com"))
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
		fabsdk.WithOrg("ben.thinking.com"))
	channelClient, err := channel.New(channelProvider)
	if err != nil {
		log.Fatalf("create channel client fail: %s\n", err.Error())
	}

	//调用链码
	request := channel.Request{
		ChaincodeID: "test_fuck_20180824004",
		Fcn:         "get_hello",
		Args:        nil,
	}
	response, err := channelClient.Query(request)
	if err != nil {
		log.Fatal("query fail: ", err.Error())
	} else {
		fmt.Printf("response is %s\n", response.Payload)
	}
}
