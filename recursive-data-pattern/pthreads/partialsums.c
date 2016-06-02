#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <pthread.h>

#define SIZE 500000000
int num_thrd = 32;

int n=SIZE, numArray[2][SIZE], *t, toread, size;


struct pair {
    unsigned int x;
    unsigned int y;
};


int min2(int x,int y){
if(x<y) return x;
return y;
}
void* multiply(void* slice)
{
    struct pair* dim = (struct pair*) slice;
    int i = dim->x;
  int j = dim->y;
  int start = (i-1)*(n/num_thrd);
  int end = min2(SIZE,start+(n/num_thrd));
  int k  = 0;
  for(i = start;i<end;i++){
        if(i - (1 << j) >= 0)
      t[i] = numArray[toread][i] + numArray[toread][i - (1 << j)];
        else t[i] = numArray[toread][i];
  }
  return 0;
}


int main()
{
int i,j;
  pthread_t* thread;
  thread = (pthread_t*) malloc((num_thrd+1)*sizeof(pthread_t));


  for(i = 0; i < n; i++)
   numArray[0][i] = i %10;

  toread = 1;

  numArray[1][0] = numArray[0][0];
  size = 0;


  while(i) {
    size++;
    i >>= 1;
  }
  for(j = 0; j < size; j++) {
    toread = !toread;
    if(toread) t = numArray[0];
    else t = numArray[1];

    for (i = 1; i <= num_thrd; i++)
      {
        struct pair *dim = malloc(sizeof(struct pair));
        dim->x = i;
        dim->y = j;
        if (pthread_create (&thread[i], NULL, multiply, dim))
        {
        printf("%d asd\n",i);
          exit(-1);
        }

      }
      for (i = 1; i <= num_thrd; i++)
       pthread_join (thread[i], NULL);
    }

  return 0;
}