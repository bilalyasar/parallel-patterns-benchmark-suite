package main

import (
	"math/rand"
	"sort"
)

func main() {
	//runtime.GOMAXPROCS(1)

	in := generateRandomInput(10000000, 5000000)
	MergeSortParallel(in)
}

func generateRandomInput(n, max int) (out []int) {
	for len(out) < n {
		out = append(out, rand.Intn(max))
	}
	return
}

func MergeSortParallel(list []int) []int {
	if len(list) <= 100 {
		sort.Ints(list)
		return list
	}

	f := func(sub []int, c chan []int) {
		c <- MergeSortParallel(sub)
	}

	lc, rc := make(chan []int), make(chan []int)
	m := len(list) / 2
	go f(list[0:m], lc)
	go f(list[m:], rc)
	return merge(<-lc, <-rc)
}

func merge(l, r []int) (result []int) {
	li, ri := 0, 0
	// copy l and r into result until nothing left to copy
	for li < len(l) || ri < len(r) {
		if li < len(l) && ri < len(r) {
			// which of l or r has smallest first element?
			if l[li] <= r[ri] {
				result = append(result, l[li])
				li++
			} else {
				result = append(result, r[ri])
				ri++
			}
		} else if li < len(l) {
			// no right sublist
			result = append(result, l[li:]...)
			return
		} else {
			// no left sublist
			result = append(result, r[ri:]...)
			return
		}
	}
	return
}

