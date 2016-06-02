package main

import (
	"sync"
)

var a = make(chan int32)
var b = make(chan int32)
var c = make(chan int32)
var wg sync.WaitGroup

func f1() {
	for {
		select {
		case msg1 := <-a:
			if msg1 == 300001 {
				wg.Done()
			}

			b <- msg1 + 1
		}
	}
}

func f2() {
	for {
		select {
		case msg1 := <-b:

			c <- msg1 + 1
		}

	}
}
func f3() {
	for {
		select {
		case msg1 := <-c:

			a <- msg1 + 1
		}

	}
}

func main() {
	wg.Add(1)
	go f1()
	go f2()
	go f3()
	go f2()
	a <- 1
	wg.Wait()

}