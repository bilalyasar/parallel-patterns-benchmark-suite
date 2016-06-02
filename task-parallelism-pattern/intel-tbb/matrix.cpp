#include <tbb/parallel_for.h>
#include <tbb/blocked_range.h>
#include <tbb/tbb.h>

using namespace tbb;
using namespace std;

const int SIZE = 1000;

float matrixA[SIZE][SIZE];
float matrixB[SIZE][SIZE];
float matrixC[SIZE][SIZE];


class Multiply
{
public:
    void operator()(blocked_range<int> r) const {
        for (int i = r.begin(); i != r.end(); ++i) {
        printf("%d ",i);
            for (int j = 0; j < SIZE; ++j) {
                for (int k = 0; k < SIZE; ++k) {
                    matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
};


int main()
{
task_scheduler_init init(2);
struct timeval tp;
gettimeofday(&tp, NULL);



long int ms = tp.tv_sec * 1000 + tp.tv_usec / 1000;

    for (int i = 0; i < SIZE; ++i) {
        for (int j = 0; j < SIZE; ++j) {
            matrixA[i][j] = i%10;
            matrixB[i][j] = i%10;
            matrixC[i][j] = 0.0f;
        }
    }
    parallel_for(blocked_range<int>(0,SIZE), Multiply());

gettimeofday(&tp, NULL);
long int msend = tp.tv_sec * 1000 + tp.tv_usec / 1000;
printf("%ld\n",msend-ms);
    return 0;
}