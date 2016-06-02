#include <stdio.h>
#include <stdlib.h>
#define NX 50000000
#define LEFTVAL 1.0
#define RIGHTVAL 10.0
#define NSTEPS 10

void initialize(double uk[], double ukp1[]) {
  uk[0] = LEFTVAL; uk[NX-1] = RIGHTVAL;
   for (int i = 1; i < NX-1; ++i)
   uk[i] = 0.0;
   for (int i=0; i < NX; ++i)
   ukp1[i] = uk[i];
   }

   int main(void) {

   double *uk = malloc(sizeof(double) * NX);
   double *ukp1 = malloc(sizeof(double) * NX);
   double *temp;

   double dx = 1.0/NX;
   double dt = 0.5*dx*dx;

   initialize(uk, ukp1);

   for (int k = 0; k < NSTEPS; ++k) {

   #pragma omp parallel for schedule(dynamic)
   for (int i = 1; i < NX-1; ++i) {
   ukp1[i]=uk[i]+ (dt/(dx*dx))*(uk[i+1]-2*uk[i]+uk[i-1]);
   }

   temp = ukp1; ukp1 = uk; uk = temp;
   }
   return 0;
   }
