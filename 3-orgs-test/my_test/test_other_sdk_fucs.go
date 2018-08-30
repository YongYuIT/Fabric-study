package my_test

import (
	"github.com/hyperledger/fabric-sdk-go/pkg/core/config"
	"github.com/hyperledger/fabric-sdk-go/pkg/fabsdk"
	"log"
)

func Other_test() {
	//读取配置文件，创建SDK
	configProvider := config.FromFile("/mnt/hgfs/3-orgs-test/my_test/config-yong.yaml")
	sdk, err := fabsdk.New(configProvider)
	if err != nil {
		log.Fatalf("create sdk fail: %s\n", err.Error())
	}
	sdk.Config()
}
