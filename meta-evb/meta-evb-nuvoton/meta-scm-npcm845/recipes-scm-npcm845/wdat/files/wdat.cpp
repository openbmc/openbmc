#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <unistd.h>
#include <poll.h>
#include <fcntl.h>
#include <stdio.h>
#include <signal.h>
#include <time.h>

#define WDAT_START	0xAA
#define WDAT_STOP	0xBB
#define WDAT_RESET	0xCC
#define WDAT_QUERY	0xDD

int kcs_fd;
int timeout = 30;
timer_t wdat_timer;

void expired(union sigval timer_data){
	printf("wdat expired\n");
	timer_delete(wdat_timer);
}

int wdat_action(int action)
{
	struct itimerspec current;
	struct sigevent sev = { 0 };
	struct itimerspec its;
	char value;


	its.it_value.tv_sec  = timeout,
	its.it_value.tv_nsec = 0,
	its.it_interval.tv_sec  = 1,
	its.it_interval.tv_nsec = 0;

	switch (action) {
	case WDAT_START:
		printf("start timer\n");
		sev.sigev_notify = SIGEV_THREAD;
		sev.sigev_notify_function = &expired;
		sev.sigev_value.sival_ptr = NULL;
		timer_create(CLOCK_REALTIME, &sev, &wdat_timer);
		timer_settime(wdat_timer, 0, &its, NULL);
		break;
	case WDAT_RESET:
		printf("refresh timer\n");
		its.it_value.tv_sec  = timeout,
		timer_settime(wdat_timer, 0, &its, NULL);
		break;
	case WDAT_STOP:
		printf("stop timer\n");
		timer_delete(wdat_timer);
		break;
	case WDAT_QUERY:
		if (timer_gettime(wdat_timer, &current) == 0)
			value = current.it_value.tv_sec;
		else
			value = 0;
		write(kcs_fd, &value, 1);
		break;
	default:
		break;
	}

	return 0;
}

int main(int argc, char *argv[])
{
	struct pollfd pfd;
	unsigned char wdat_cmd;
	int r;

	kcs_fd = open(argv[1], O_RDWR | O_NONBLOCK);
	if (kcs_fd < 0) {
		return -1;
	}
	pfd.fd = kcs_fd;

	pfd.events = POLLIN;

	while (1) {
		r = poll(&pfd, 1, 5000);

		if (r < 0) {
			break;
		}

		if (r == 0 || !(pfd.revents & POLLIN)) {
			continue;
		}

		r = read(pfd.fd, &wdat_cmd, 1);
		if (r <= 0) {
			continue;
		}

		wdat_action(wdat_cmd);
	}

	close(pfd.fd);
	return 0;
}
