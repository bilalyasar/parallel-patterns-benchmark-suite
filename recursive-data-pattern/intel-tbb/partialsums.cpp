#include <iostream>
#include <stdlib.h>
#include <time.h>
#include "tbb/task_scheduler_init.h"
#include "tbb/blocked_range.h"
#include "tbb/parallel_scan.h"
#include "tbb/tick_count.h"

using namespace std;
using namespace tbb;

template <class T>
class Body
{
    T final_result;
    T* const y;
    const T* const x;

    public:

    Body( T y_[], const T x_[] ) : final_result(0), x(x_), y(y_) {}

    T get_final_result() const {return final_result;}

    template<typename Tag>
    void operator()( const blocked_range<int>& r, Tag )
    {
        T temp = final_result;
        int rend = r.end();

        for( int i=r.begin(); i<rend; ++i )
        {
            temp = temp+x[i];
            if( Tag::is_final_scan() )
            y[i] = temp;
        }

        final_result = temp;
    }

    Body( Body& b, split ) : x(b.x), y(b.y), final_result(10) {}

    void reverse_join( Body& a )
    {
        final_result = a.final_result + final_result;
    }

    void assign( Body& b )
    {
        final_result = b.final_result;
    }
};


template<class T>
float DoPartialSum( T y[], const T x[], int n)
{
    Body<int> body(y,x);
    tick_count t1,t2,t3,t4;
    t1=tick_count::now();
    parallel_scan( blocked_range<int>(0,n), body , auto_partitioner() );
    t2=tick_count::now();
    cout<<(t2-t1).seconds()<<endl;
    return body.get_final_result();
}



int main()
{
    int *y1=(int*)malloc(150000000 * sizeof(int));
    int *x1=(int*)malloc(150000000 * sizeof(int));

    for(int i=0;i<150000000;i++)
        x1[i]=(i%10);
    DoPartialSum(y1,x1,150000000);
    return 0;
}