package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"os/exec"
	"strconv"
)

type PrInfo struct {
	Action    string `json:"action"`
	PrPayload `json:"pull_request"`
}

type PrPayload struct {
	Id int `json:"number"`
}

func (p PrPayload) ProjectName() string {
	projectName := "ci-refs-pull-" + strconv.Itoa(p.Id) + "-" + "merge"
	return projectName
}

func CleanupPr(w http.ResponseWriter, r *http.Request) {
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		panic(err)
	}
	r.Body.Close()
	var prInfo PrInfo
	if err := json.Unmarshal(body, &prInfo); err != nil {
		panic(err)
	}
	if prInfo.Action == "closed" {
		if err := ioutil.WriteFile("project_to_delete.txt", []byte(prInfo.ProjectName()), 0644); err != nil {
			panic(err)
		}
		defer os.Remove("project_to_delete.txt")
		cleanupCmd := exec.Command("/bin/bash", "-c", "./cleanup.sh")
		if err := cleanupCmd.Run(); err != nil {
			fmt.Fprintln(os.Stderr, "cleanup of "+prInfo.ProjectName()+" failed:", err)
		}
	}
}

func main() {
	http.HandleFunc("/", CleanupPr)
	if err := http.ListenAndServe(":8080", nil); err != nil {
		panic(err)
	}
}
