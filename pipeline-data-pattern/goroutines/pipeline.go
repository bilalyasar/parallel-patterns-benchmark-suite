package main

import (
	"fmt"
	"runtime"
	"sync"
	"math/rand"
	"time"
)

const N int64 = 10

var NPROCS int = 5
var SIZE int = 500000
var a [N]int64
var wg sync.WaitGroup
var wg1 sync.WaitGroup

func write(i int64) {
	fmt.Println("hello ", i);
	defer wg.Done()
}
func send(x chan int) {
	x <- 5
}

func putWork_stage1(stage1 chan int) {
	for i := 1; i < SIZE; i++ {
		stage1 <- (rand.Int() % 1000)
	}
	close(stage1)
	wg.Done()
}
func getWork_stage1(stage1 chan int, stage2 chan int) {
	for {
		msg, more := <-stage1
		if (more) {
			stage2 <- msg
			//fmt.Println("received")
		}
		if (!more) {
			break
		}
	}
	close(stage2)
	wg.Done()

}

func getWork_stage2(stage1 chan int, stage2 chan int) {
	for {
		_, more := <-stage2
		if (more) {

		}
		if (!more) {
			break
		}
	}
	wg.Done()

}

func main() {
	var t = time.Now()
	stage1 := make(chan int);
	stage2 := make(chan int);
	runtime.GOMAXPROCS(3)
	wg.Add(3)
	go putWork_stage1(stage1)
	go getWork_stage1(stage1, stage2)
	go getWork_stage2(stage1, stage2)

	wg.Wait()
	var duration = time.Since(t)
	fmt.Println(duration.Seconds())

}