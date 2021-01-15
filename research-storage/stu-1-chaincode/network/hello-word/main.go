package main
 
import (
	pb "github.com/hyperledger/fabric/protos/peer"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"fmt"
	"net"
	"os"
	"strconv"
)
 
type MyCode struct {
}
 
func (this *MyCode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	ip, err := GetIp()
	if err != nil {
		return shim.Error("get ip error")
	}
	var result pb.Response
	result.Status = shim.OK
	result.Payload = []byte("Invoke on -> " + ip + " --> " + strconv.Itoa(os.Getegid()))
	return result
 
}
 
func (this *MyCode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	ip, err := GetIp()
	if err != nil {
		return shim.Error("get ip error")
	}
	var result pb.Response
	result.Status = shim.OK
	result.Payload = []byte("Invoke on -> " + ip + " --> " + strconv.Itoa(os.Getegid()))
	return result
}
 
func main() {
	err := shim.Start(new(MyCode))
	if err != nil {
		fmt.Println("fuck error -> " + err.Error())
	}
}
 
func GetIp() (string, error) {
	adds, err := net.InterfaceAddrs()
	if err != nil {
		return "", fmt.Errorf("cannot read ip")
	}
	for _, address := range adds {
		if ipnet, ok := address.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ipnet.IP.To4() != nil {
				return ipnet.IP.To4().String(), nil
			}
		}
	}
	return "", fmt.Errorf("no ip")
}

