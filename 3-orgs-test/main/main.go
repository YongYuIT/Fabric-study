package main

import (
	"fmt"
	"os/exec"
	"os"
	"path/filepath"
	"../my_test"
)

func main() {
	file, err := exec.LookPath(os.Args[0])
	if err == nil {
		path, err := filepath.Abs(file)
		if err == nil {
			fmt.Println(path)
		}
	}
	my_test.Call_with_yong()
}
