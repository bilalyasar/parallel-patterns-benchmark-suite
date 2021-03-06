#include<stdlib.h>
#include<stdio.h>
#include<pthread.h>

int	q[500000];						// queue to hold produced numbers
int size =500000;
int	first  = 0;					// index into q of next free space
int last   = 0;					// index into q of next available number
int	numInQ = 0;					// number of items in the queue
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;	// mutual exclusion lock
pthread_mutex_t empty = PTHREAD_MUTEX_INITIALIZER;	// synchronization lock

/* putOnQ is called by the producer threads to put a number on the queue */
void putOnQ(int x) {
	pthread_mutex_lock(&mutex);		// lock access to the queue
	q[first] = x;					// put item on the queue
	first = (first+1) % 50000;
	numInQ++;						// increment queue size
	pthread_mutex_unlock(&mutex);	// unlock queue
	pthread_mutex_unlock(&empty);	// start a waiting consumer

}

/* getOffQ is called by consumer threads to retrieve a number
   from the queue.  The thread will be suspended if there is
   nothing on the queue. */
int getOffQ(void) {
	int thing;						// item removed from the queue
	/* wait if the queue is empty. */
	printf("%d\n",numInQ);
	while (numInQ == 0) pthread_mutex_lock(&empty);
    pthread_mutex_lock(&mutex);		// lock access to the queue
    thing = q[last];				// get item from the queue
    last = (last+1) % 50000;
    numInQ--;						// decrement queue size
//    printf("%d\n",numInQ);
    pthread_mutex_unlock(&mutex);	// unlock queue
	return thing;
}


/*  Producer thread. */
void * prod(void *arg) {
	int	mynum;						// thread number for identification
	int	i;

	mynum = *(int *)arg;
	for (i = 0; i < size; i++) {
//		sleep(i);					// wait just to make this interesting
//		puts("putting");
		putOnQ(i+100*mynum);		// put number on the queue
//		printf("put %d on queue\n", i);
	}
 }


/*  Consumer thread  */
void * con(void *arg) {
	int	i;
	int	stuff;						// number received from queue
	for (i = 0; i < size-100; i++) {
//		puts("getting");
		stuff = getOffQ();			// get number from the queue
//		printf("got %d from queue\n", stuff);
	}
	pthread_mutex_unlock(&mutex);
	pthread_mutex_unlock(&empty);
	exit(0);
 }



void main()
{
 pthread_t threadp1;				// Thread objects
 pthread_t threadp2;
 pthread_t threadc1;
 int zero = 0;  int one = 1;

 pthread_create(&threadp1, NULL, prod, &zero);	// start first producer thread
// pthread_create(&threadp2, NULL, prod, &one);	// start second producer thread
// sleep(1);
 pthread_create(&threadc1, NULL, con,  NULL);	// start consumer thread
// pthread_join(threadp2,NULL);
 pthread_join(threadc1,NULL);
  pthread_join(threadp1,NULL);
 pthread_exit(NULL);							// main thread quits
}