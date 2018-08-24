package main

import (
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"strings"
)

type MyCode struct {
	myName string
}

func (this *MyCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	str, str_arry := stub.GetFunctionAndParameters()
	str = "init str->" + str + "\n"
	for index, value := range str_arry {
		str += fmt.Sprintf("init value %d --> %s\n", index, value)
	}
	var result pb.Response
	result.Status = shim.OK
	result.Payload = []byte(str)
	return result
}

func (this *MyCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	str, str_arry := stub.GetFunctionAndParameters()
	var result pb.Response
	if (strings.Compare("set_hello", str) == 0) {
		hello_word := str_arry[0]
		err := stub.PutState("hello_word", []byte(hello_word))
		if err != nil {
			return shim.Error("PutState error")
		}
		result.Status = shim.OK
		result.Payload = []byte("set success --> " + hello_word)
		return result
	}
	if (strings.Compare("get_hello", str) == 0) {
		hello_word, err := stub.GetState("hello_word")
		if err != nil {
			return shim.Error("GetState error")
		}
		result.Status = shim.OK
		result.Payload = []byte("get success --> " + string(hello_word))
		return result
	}

	return shim.Error("no func error")
}

func main() {
	err := shim.Start(new(MyCode))
	if err != nil {
		fmt.Println("fuck error -> " + err.Error())
	}
}
