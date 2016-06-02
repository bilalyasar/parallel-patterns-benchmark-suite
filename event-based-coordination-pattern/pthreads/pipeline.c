#include<stdlib.h>
#include<stdio.h>
#include<pthread.h>

// number of items in the queue
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;	// mutual exclusion lock
pthread_mutex_t empty = PTHREAD_MUTEX_INITIALIZER;	// synchronization lock
pthread_mutex_t mutex2 = PTHREAD_MUTEX_INITIALIZER;	// mutual exclusion lock
pthread_mutex_t empty2 = PTHREAD_MUTEX_INITIALIZER;	// synchronization lock
int numInQ2,numInQ;
int size = 300000;
/* putOnQ is called by the producer threads to put a number on the queue */
void putOnQ(int x) {
	pthread_mutex_lock(&mutex);		// lock access to the queue
    printf("%d\n",x);
	   numInQ+=1;
	pthread_mutex_unlock(&mutex);	// unlock queue
	pthread_mutex_unlock(&empty);	// start a waiting consumer

}
void putOnQ2(int x) {
	pthread_mutex_lock(&mutex2);		// lock access to the queue

	numInQ2+=1;
	pthread_mutex_unlock(&mutex2);	// unlock queue
	pthread_mutex_unlock(&empty2);	// start a waiting consumer

}

/* getOffQ is called by consumer threads to retrieve a number
   from the queue.  The thread will be suspended if there is
   nothing on the queue. */
void getOffQ(void) {

	while (numInQ == 0) pthread_mutex_lock(&empty);
    pthread_mutex_lock(&mutex);		// lock access to the queue
    numInQ-=1;
    pthread_mutex_unlock(&mutex);	// unlock queue
//	return thing;
}
void getOffQ2(void) {
	int thing;						// item removed from the queue
	/* wait if the queue is empty. */
	printf("%d\n",numInQ2);
	while (numInQ2 == 0) pthread_mutex_lock(&empty2);
    pthread_mutex_lock(&mutex2);		// lock access to the queue
    numInQ2-=1;
    pthread_mutex_unlock(&mutex2);	// unlock queue
//	return thing;
}

/*  Producer thread. */
void * prod(void *arg) {
	int	mynum;						// thread number for identification
	int	i;

	mynum = *(int *)arg;
	for (i = 0; i < size; i++) {
//		sleep(i);					// wait just to make this interesting
//		puts("putting");
		putOnQ(i);
//		printf("put %d on queue\n", i);
	}
 }


/*  Consumer thread  */
void * con(void *arg) {
	int	i;
	int	stuff;						// number received from queue
	for (i = 0; i < size; i++) {
//		puts("getting");

		getOffQ();			// get number from the queue
		putOnQ2(i);
//		printf("got %d from queue\n", stuff);
	}
	pthread_mutex_unlock(&mutex);
	pthread_mutex_unlock(&empty);
	exit(0);
 }
void * con2(void *arg) {
	int	i;

	for (i = 0; i < size; i++) {
	printf("%d**\n",i);
		getOffQ2();			// get number from the queue
//		printf("got %d from queue\n", stuff);
	}
	pthread_mutex_unlock(&mutex2);
	pthread_mutex_unlock(&empty2);
	exit(0);
 }


void main()
{
 pthread_t threadp1;				// Thread objects
 pthread_t threadc2;
 pthread_t threadc1;
 int zero = 0;  int one = 1;

 pthread_create(&threadp1, NULL, prod, &zero);	// start first producer thread
// pthread_create(&threadp2, NULL, prod, &one);	// start second producer thread
// sleep(1);
 pthread_create(&threadc1, NULL, con,  NULL);	// start consumer thread
 pthread_create(&threadc2, NULL, con2,  NULL);	// start consumer thread
// pthread_join(threadp2,NULL);
 pthread_join(threadc1,NULL);
  pthread_join(threadp1,NULL);
  pthread_join(threadc2,NULL);
 pthread_exit(NULL);							// main thread quits
}