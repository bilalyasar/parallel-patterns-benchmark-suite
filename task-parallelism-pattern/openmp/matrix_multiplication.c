#include <omp.h>
#include <stdio.h>
#include <stdlib.h>

#define SIZE 2000
int	matrixA[SIZE][SIZE],
	matrixB[SIZE][SIZE],
	matrixC[SIZE][SIZE];

int main (int argc, char *argv[]) 
{
int	tid, i, j, k, chunk = 500 , val = 0;

  for (i=0; i<SIZE; i++)
    for (j=0; j<SIZE; j++)
      matrixA[i][j]= (val++)%10;
      val = 0;
  for (i=0; i<SIZE; i++)
    for (j=0; j<SIZE; j++)
      matrixB[i][j]= (val++)%10;
  for (i=0; i<SIZE; i++)
    for (j=0; j<SIZE; j++)
      matrixC[i][j]= 0;
if(argc>1) omp_set_num_threads(atoi(argv[1]));
  else omp_set_num_threads(4);
  #pragma omp parallel shared(matrixA,matrixB,matrixC,chunk) private(tid,i,j,k)
  {
  #pragma omp for schedule (dynamic, chunk)
  for(i=0; i<SIZE; i++)
    {
    printf("%d ",i);
    for(j=0; j<SIZE; j++)
      for (k=0; k<SIZE; k++)
        matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
    }
}
}
