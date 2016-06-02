#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#define NX 50000000
#define LEFTVAL 1.0
#define RIGHTVAL 10.0
#define NSTEPS 10
int num_thrd = 32;
double *uk,*ukp1 ;
   double dx;
   double dt;

int min2(int x,int y){
    if(x<y) return x;
    return y;
}
void* multiply(void* slice)
{
int i =0;
  int s = (int)slice;
  int start = (NX / (num_thrd))*(s-1);
  int end = min2(start + NX/num_thrd,NX);
  printf("%d %d %d\n",s,start,end);
  for(i=start;i<end;i++)
ukp1[i]=uk[i]+ (dt/(dx*dx))*(uk[i+1]-2*uk[i]+uk[i-1]);
}


void initialize(double uk[], double ukp1[]) {
int i=0;
  uk[0] = LEFTVAL; uk[NX-1] = RIGHTVAL;
   for (i = 1; i < NX-1; ++i)
   uk[i] = 0.0;
   for (i=0; i < NX; ++i)
   ukp1[i] = uk[i];
   }



   int main(void) {
dx = 1.0/NX;
dt = 0.5*dx*dx;
    pthread_t* thread;

    thread = (pthread_t*) malloc((num_thrd+1)*sizeof(pthread_t));


   /* pointers to arrays for two iterations of algorithm */
   uk = (double *)malloc(sizeof(double) * (NX+2));
   ukp1 = (double *)malloc(sizeof(double) * (NX+2));
   double *temp;

   initialize(uk, ukp1);
    int i,k;
   for ( k = 0; k < NSTEPS; ++k) {

for (i = 1; i < num_thrd; i++)
  {
    if (pthread_create (&thread[i], NULL, multiply, (void*)i))
    {
    printf("error\n");
      exit(-1);
    }
  }

  for (i = 1; i < num_thrd; i++) pthread_join (thread[i], NULL);

   /* "copy" ukp1 to uk by swapping pointers */
   temp = ukp1; ukp1 = uk; uk = temp;

   }
   return 0;
   }
