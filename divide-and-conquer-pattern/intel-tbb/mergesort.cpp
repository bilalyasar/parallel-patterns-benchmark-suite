#include <iostream>
#include <iterator>
#include <vector>
#include <algorithm>
#include <cstddef>
#include <fstream>
#include <tbb/parallel_invoke.h>

using namespace std;
size_t arr_size =8388608;
  int arr[8388608];
  
  
  void Merge(int *data, int left, int mid, int right) {
  int sorted_data[right - left + 1];
  std::size_t sorted_idx = 0;
  auto first_begin = left;
  const auto first_end = mid;
  auto second_begin = mid + 1;
  const auto second_end = right;

  while (first_begin <= first_end && second_begin <= second_end) {
    if (data[first_begin] <= data[second_begin]) {
      sorted_data[sorted_idx++] = data[first_begin++];
    }
    else {
      sorted_data[sorted_idx++] = data[second_begin++];
    }
  }

  while (first_begin <= first_end) {
    sorted_data[sorted_idx++] = data[first_begin++];
  }

  while (second_begin <= second_end) {
    sorted_data[sorted_idx++] = data[second_begin++];
  }

  for (std::size_t i = 0; i < sorted_idx; ++i) {
    data[left + i] = sorted_data[i];
  }
}

void MergeSort(int *data, int left, int right) {
  if (left < right) {
    auto mid = left + (right - left) / 2;
    tbb::parallel_invoke([=]() { MergeSort(data, left, mid); }, [=]() { MergeSort(data, mid + 1, right); });
    Merge(data, left, mid, right);
  }
}

int main() {
  std::size_t elem_q = arr_size;
  MergeSort(arr, 0, elem_q - 1);

  return 0;
}