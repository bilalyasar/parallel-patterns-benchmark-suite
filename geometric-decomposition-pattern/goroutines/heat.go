package main

import "fmt"

const NX = int32(50000000)
const LEFTVAL = 1.0
const RIGHTVAL = 10.0
const NSTEPS = 10

var temp, uk, ukp1 *[]float32
var ukk, ukp11 []float32
var dx = float32(1.0 / NX)

var dt = float32(0.5 * dx * dx)

func initialize() {
	(*uk)[0] = LEFTVAL
	(*uk)[NX - 1] = RIGHTVAL
	for i := int32(1); i < NX - 1; i++ {
		(*uk)[i] = 0.0;
	}
	for i := int32(0); i < NX; i++ {
		(*ukp1)[i] = (*uk)[i];
	}
}

func f1(pid, start, end int32) {
	fmt.Println(start,end)
	for i := start; i <= end; i++ {
		(*ukp1)[i] = (*uk)[i] + (dt / (dx * dx)) * ((*uk)[i + 1] - 2 * (*uk)[i] + (*uk)[i - 1]);
	}
}
func min(x, y int32) int32 {
	if x > y {
		return y
	}
	return x

}
func main() {
	var start, end = int32(1), int32(1)
	ukk = make([]float32, NX+1);
	uk = &ukk
	ukp11 = make([]float32, NX+1);
	ukp1 = &ukp11
	initialize();
	for k := 0; k < NSTEPS; k++ {
		start,end =1,1
		for i := int32(0); i < 32; i++ {
			end = min(end + (NX - 1) / 32, NX - 1)
			//fmt.Println(start,end)
			go f1(i, start, end)
			start = end + 1
		}
		temp = ukp1
		ukp1 = uk
		uk = temp
		//
		//#pragma omp parallel for schedule(static)
		///* compute new values */
		//for (int i = 1; i < NX-1; ++i) {
		//ukp1[i]=uk[i]+ (dt/(dx*dx))*(uk[i+1]-2*uk[i]+uk[i-1]);
		//}
		//
		///* "copy" ukp1 to uk by swapping pointers */

		//
		//printValues(uk, k);
	}
}