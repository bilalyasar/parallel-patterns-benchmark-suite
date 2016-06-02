
#include <stdio.h>
#include <stdlib.h>

#define MAXWORK 300000
int a,b,c,d,msg1,msg2,msg3;
int main( ){

omp_set_num_threads(3);

 #pragma omp parallel shared(a,b,c,d,msg1,msg2,msg3)
   {

   #pragma omp sections nowait
     {

     #pragma omp section
    {
        int i=0;
        for(i=0;i<MAXWORK;i++){
        #pragma omp critical
        msg1++;
        }
    }

     #pragma omp section
     {
        int k=0;
        while(k<MAXWORK-1){
            if(msg1<=0) sched_yield();
            else{
//                printf("%d %d\n",msg1,k);
                #pragma omp critical
                msg1--;
                #pragma omp critical
                msg2++;
                k++;
            }

        }
     }
     #pragma omp section
          {
                    int k=0;
                    while(k<MAXWORK-1){
                        if(msg2<=0) sched_yield();
                        else{
                        #pragma omp critical
                            msg2--;
                            #pragma omp critical
                            msg3++;
                            k++;
                        }

                    }
          }

     }  /* end of sections */

   }  /* end of parallel region */

}
