#include <stdio.h>
#include <omp.h>

#define ARRAYSIZE 150000000
int n=ARRAYSIZE, number_array[2][ARRAYSIZE+1], *t, i, current, log_size, j;
int main()
{


  for(i = 0; i < n; i++) number_array[0][i] = i %10;
  current = 1;
  number_array[1][0] = number_array[0][0];
  
  while(i) {
    log_size++;
    i >>= 1;
  }
  omp_set_num_threads(32);
  for(j = 0; j < log_size; j++) {
    current = !current;

    if(current) t = number_array[0];
    else t = number_array[1];
#pragma omp parallel for schedule(dynamic,10000) private(i) shared(n, j, t, number_array, current)
    for(i = 1; i < n; i++) {

      if(i - (1 << j) >= 0)
    t[i] = number_array[current][i] + number_array[current][i - (1 << j)];
      else t[i] = number_array[current][i];
    }

  }

  current = !current;
  return 0;
}