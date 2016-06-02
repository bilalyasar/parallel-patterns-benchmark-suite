package main

import (
	"runtime"
	"sync"
	"time"
	"fmt"
)

var matrix [][]int
var wg sync.WaitGroup
var SIZE int = 1000
var result_matrix [][]int

func main() {
	t := time.Now()
	runtime.GOMAXPROCS(10)

	matrix = make([][]int, SIZE)
	for i := range matrix {
		matrix[i] = make([]int, SIZE)
	}

	result_matrix = make([][]int, SIZE)
	for i := range result_matrix {
		result_matrix[i] = make([]int, SIZE)
	}
	wg.Add(SIZE)
	for i := 0; i < SIZE; i++ {
			go f1(i)
		}
	wg.Wait()
	var duration = time.Since(t);
	fmt.Println(duration.Seconds())
}
func f1(i int) (int) {
	res := 0;
	for j := 0; j < SIZE; j++ {
		res :=0
		for number := 0; number < SIZE; number++ {
			res += matrix[i][number] * matrix[number][j]
			result_matrix[i][j] = res
		}
	}
	defer wg.Done()
	return res
}
