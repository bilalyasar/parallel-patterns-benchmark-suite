
#include <stdio.h>
#include <stdlib.h>

#define MAXWORK 500000

int work[MAXWORK],  // work to be done
    nwork1=0,nwork2=0,  // number of items in the queue
    nextput1=0,  // producer will place number # at work[nextput]
    nextget1=0,  // consumer will obtain next # at work[nextget]
    nextput2=0,  // producer will place number # at work[nextput]
    nextget2=0,  // consumer will obtain next # at work[nextget]
    breaksum,  // sum after which everyone stops
    done = 0,  // value 1 signals producer exceeded breaksum
    psum,csum,  // sums found by producer, consumers
    stage1_work[MAXWORK],  // work done by producer
    stage2_work[MAXWORK],
    *cwork,  // work done by the consumers
    nth;  // number of threads


void putwork_stage2 (int k)
{  int put = 0;
   while (!put)  {
         #pragma omp critical
         {  
            stage2_work[nextput2] = k;
            nextput2++;
            put = 1;
         }
      }
}

void putwork_stage1(int k)
{  

   int put = 0;
   while (!put)  {
         #pragma omp critical
         {  
            stage1_work[nextput1] = k;
            nextput1++;
            if(nextput1%10000 == 0)printf("%d ",nextput1);
            put = 1;
         }
   }
}

int getwork_stage1()
{  
   int k,get=0;
   while (!get)  {
      if (nextget1 < nextput1)  {
         #pragma omp critical
         {  
               k = stage1_work[nextget1];
               nextget1++;
               get = 1;
            }
      }
      else return -1;
   }
   return k;
}


int getwork_stage2()
{  
   int k,get=0;
   while (!get)  {
      if( nextget2 == nextput2 && nwork1 ==MAXWORK) return -1;
      if (nextget2 < nextput2)  {
         #pragma omp critical
         {  
               k = stage1_work[nextget2];
               nextget2++;
               get = 1;
            }
      }
      else sched_yield();
   }
   return k;
}

void dowork()
{
   int num =0;
   
   #pragma omp parallel private(num)
   {  int me = omp_get_thread_num();
      nth =  omp_get_num_threads();
      if (me < nth/2)  {  // stage1 workers
      //   pwork = 0;
         while (1)  {
            num = getwork_stage1();
//            printf("%d\n",num);
            if(num == -1) break;
            if(num % 2 == 1) putwork_stage2(num);
             #pragma omp atomic
             nwork1++;
         }
      }
      else  {  //  stage2 workers
         while (1)  {
            num = getwork_stage2();
//            printf("%d\n",num);
            if(num == -1) break;
            #pragma omp atomic
            csum += num;
            #pragma omp atomic
            nwork2++;
         }
      }
   }
}

int main(){
  omp_set_num_threads(2);
   int i;
   int num;
//   #pragma omp parallel  for private(num)
   for(i = 0 ; i < MAXWORK; i ++){
   num = rand() % 100;
   putwork_stage1(num);

   }
   dowork();
//   printf("%d\n",csum);
}
