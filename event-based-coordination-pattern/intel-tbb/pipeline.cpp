#include <iostream>

#include "tbb/concurrent_queue.h"
#include "tbb/tick_count.h"
#include "tbb/task_scheduler_init.h"
#include "tbb/task_group.h"

using namespace tbb;
using namespace std;

#define QUEUE_T concurrent_queue<int>
#define ELEMENTS_COUNT  300000

task_group *t_group;


void producer(QUEUE_T *q) ;
void consumer(QUEUE_T *q,QUEUE_T *p);
void consumer2(QUEUE_T *q,QUEUE_T *p);

class producer_helper_functor {
    QUEUE_T *queue;
public:
    producer_helper_functor(QUEUE_T *q) : queue(q) {};
    void operator() () const { producer(queue); }
};


class consumer_helper_functor {
    QUEUE_T *queue;
    QUEUE_T *queue2;
public:
    consumer_helper_functor(QUEUE_T *q,QUEUE_T *p) : queue(q), queue2(p) {};
    void operator() () const { consumer(queue,queue2); }
};

class consumer2_helper_functor {
    QUEUE_T *queue;
    QUEUE_T *queue2;
public:
    consumer2_helper_functor(QUEUE_T *q,QUEUE_T *p) : queue(q),queue2(p) {};
    void operator() () const { consumer2(queue,queue2); }
};


void producer(QUEUE_T *q) {
    for (int i = 0; i < ELEMENTS_COUNT; i++)
        q->push(i);
}

void consumer(QUEUE_T *q,QUEUE_T *p) {
    for (int i = 0; i < ELEMENTS_COUNT; i++) {
    	int j;
        while (!q->try_pop(j))
        	;/* busy wait on next element (lock contention?) */
        	p->push(i);
        if (i != j)
            cout << "Unexpected value: got " << j << " while expecting " << i << endl;
    }

}

void consumer2(QUEUE_T *q,QUEUE_T *p) {
    for (int i = 0; i < ELEMENTS_COUNT; i++) {
    	int j;
        while (!p->try_pop(j))
        	;/* busy wait on next element (lock contention?) */
        if (i != j)
            cout << "Unexpected value: got " << j << " while expecting " << i << endl;
    }

}

int main(int argc, char *argv[]) {

    task_scheduler_init(3);
    t_group = new task_group;

    QUEUE_T *QUEUE,*QUEUE1;
    QUEUE = new QUEUE_T();
    QUEUE1 = new QUEUE_T();


    tick_count t0 = tick_count::now();

    t_group->run(producer_helper_functor(QUEUE));
    t_group->run(consumer_helper_functor(QUEUE,QUEUE1));
    t_group->run(consumer2_helper_functor(QUEUE,QUEUE1));

    t_group->wait();

    tick_count t1 = tick_count::now();

    cout << "Total time is " << (t1 - t0).seconds() * 1000 << "ms" << endl;

    delete QUEUE;
    return 0;
}