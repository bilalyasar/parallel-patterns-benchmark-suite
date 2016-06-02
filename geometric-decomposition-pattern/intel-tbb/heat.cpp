
#include <cstdio>
#include <cstdlib>
#include <tbb/parallel_for.h>
#include <tbb/blocked_range.h>

using namespace tbb;
#define NX 50000000
#define LEFTVAL 1.0
#define RIGHTVAL 10.0
#define NSTEPS 10
double *uk,*ukp1,dx,dt;

class Multiply
{
public:
    void operator()(blocked_range<int> r) const {
        for (int i = r.begin(); i != r.end(); ++i) {

           ukp1[i]=uk[i]+ (dt/(dx*dx))*(uk[i+1]-2*uk[i]+uk[i-1]);

        }
    }
};


void initialize(double uk[], double ukp1[]) {
  uk[0] = LEFTVAL; uk[NX-1] = RIGHTVAL;
   for (int i = 1; i < NX-1; ++i)
   uk[i] = 0.0;
   for (int i=0; i < NX; ++i)
   ukp1[i] = uk[i];
   }

   void printValues(double uk[], int step) { /* NOT SHOWN */ }

   int main(void) {
   /* pointers to arrays for two iterations of algorithm */
   uk = (double *) malloc(sizeof(double) * NX);
   ukp1 = (double *) malloc(sizeof(double) * NX);
   double *temp;

   dx = 1.0/NX;
   dt = 0.5*dx*dx;

   initialize(uk, ukp1);

   for (int k = 0; k < NSTEPS; ++k) {

parallel_for(blocked_range<int>(1,NX-1), Multiply());
   temp = ukp1; ukp1 = uk; uk = temp;

   printValues(uk, k);
   }
   return 0;
   }
