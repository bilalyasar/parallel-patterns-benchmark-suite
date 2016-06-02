#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
 
#define SIZE 10000
int num_thrd =8;
 
int matrixA[SIZE][SIZE], matrixB[SIZE][SIZE], matrixC[SIZE][SIZE];
 

void generateMatrix(int m[SIZE][SIZE])
{
  int i, j, val = 0;
  for (i = 0; i < SIZE; i++)
    for (j = 0; j < SIZE; j++)
      m[i][j] = (++val)%10;
}
void* multiply_task(void* slice)
{
  int s = (int)slice;
  int from = (s * SIZE)/num_thrd;
  int to = ((s+1) * SIZE)/num_thrd;
  int i,j,k;

  for (i = from; i < to; i++)
  {  
    for (j = 0; j < SIZE; j++)
    {
      matrixC[i][j] = 0;
      for ( k = 0; k < SIZE; k++)
        matrixC[i][j] += matrixA[i][k]*matrixB[k][j];
    }
  }
  return 0;
}
 
int main( )
{
  pthread_t* thread;

  generateMatrix(matrixA);
  generateMatrix(matrixB);
  thread = (pthread_t*) malloc(num_thrd*sizeof(pthread_t));
  int i;
  for (i = 1; i < num_thrd; i++)
  {
    if (pthread_create (&thread[i], NULL, multiply_task, (void*)i) != 0 )
    {
      exit(-1);
    }
  }

  multiply_task(0);

  for (i = 1; i < num_thrd; i++)
 pthread_join (thread[i], NULL);
 
  return 0;
 
}
