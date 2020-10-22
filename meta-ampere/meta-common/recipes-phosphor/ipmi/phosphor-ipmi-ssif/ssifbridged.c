/*
 * Copyright (c) 2018-2020 Ampere Computing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This is a daemon that forwards requests and receive responses from SSIF over
 * the D-Bus IPMI Interface.
 *
 * This daemon is based on btbridged from https://github.com/openbmc/btbridge
 * There is no need to queue messages for SSIF since they should not arrive out
 * of sequence.
 * The timer is used as we want to abort 'stuck' commands after an expiry time.
 * Messages that are received out of order are discarded.
 */

#include <assert.h>
#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <limits.h>
#include <poll.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <syslog.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <sys/timerfd.h>
#include <time.h>
#include <unistd.h>

#include <linux/ssif-bmc.h>

#include <systemd/sd-bus.h>

static const char *ssif_bmc_device = "/dev/ipmi-ssif-host";

#define PREFIX "SSIF_BRIDGED"

#define SSIF_BMC_PATH ssif_bmc_device
#define SSIF_BMC_TIMEOUT_SEC 2
#define SSIF_MAX_REQ_LEN 254
#define SSIF_MAX_RESP_LEN 254

/* Completion code specifies a command times out */
#define IPMI_CC_CANNOT_PROVIDE_RESP 0xce


#define DBUS_NAME "org.openbmc.HostIpmi"
#define OBJ_NAME "/org/openbmc/HostIpmi/1"

#define SD_BUS_FD 0
#define SSIF_FD 1
#define TIMER_FD 2
#define TOTAL_FDS 3

#define MSG_OUT(f_, ...) do { \
	if (verbosity != SSIF_LOG_NONE) { \
		ssif_log(LOG_INFO, f_, ##__VA_ARGS__); \
	} \
} while(0)
#define MSG_ERR(f_, ...) do { \
	if (verbosity != SSIF_LOG_NONE) { \
		ssif_log(LOG_ERR, f_, ##__VA_ARGS__); \
	} \
} while(0)

struct ipmi_msg {
	uint8_t netfn;
	uint8_t lun;
	uint8_t seq;
	uint8_t cmd;
	uint8_t cc; /* Only used on responses */
	uint8_t *data;
	size_t data_len;
};

struct ssifbridged_context {
	/* The file descriptors to poll */
	struct pollfd fds[TOTAL_FDS];

	/* Pointer to sdbus */
	struct sd_bus *bus;

	/* Tracking variable for a pending message so that if it times out,
	 * we can send a response using the correct lun, netfn and command to
	 * indicate to the host that we timed out waiting for a response
	 */
	struct ipmi_msg ssif_pending_msg;

	/* Flag to indicate whether we are awaiting a response */
	int awaiting_response;
};

static void (*ssif_vlog)(int p, const char *fmt, va_list args);
static int running = 1;
static enum {
	SSIF_LOG_NONE = 0,
	SSIF_LOG_VERBOSE,
	SSIF_LOG_DEBUG
} verbosity;

static void ssif_log_console(int p, const char *fmt, va_list args)
{
	struct timespec time;
	FILE *s = (p < LOG_WARNING) ? stdout : stderr;

	clock_gettime(CLOCK_REALTIME, &time);

	fprintf(s, "[%s %ld.%.9ld] ", PREFIX, time.tv_sec, time.tv_nsec);

	vfprintf(s, fmt, args);
}

	__attribute__((format(printf, 2, 3)))
static void ssif_log(int p, const char *fmt, ...)
{
	va_list args;

	va_start(args, fmt);
	ssif_vlog(p, fmt, args);
	va_end(args);
}

static struct ipmi_msg *ssif_msg_create(struct ssifbridged_context *context,
		uint8_t *ssif_data)
{
	int len;

	assert(context && ssif_data);

	/*
	 * len here is the length of the array.
	 * Helpfully SSIF doesn't count the length byte
	 */
	len = ssif_data[0] + 1;

	if (len < 3) {
		MSG_ERR("Trying to get SSIF message with a short length (%d)\n",
				len);
		return NULL;
	}

	MSG_OUT("Trying to get SSIF message with len (%d)\n", len);

	context->ssif_pending_msg.data_len = len - 3;
	/* Don't count the lenfn/ln, seq and command */
	context->ssif_pending_msg.netfn = ssif_data[1] >> 2;
	context->ssif_pending_msg.lun = ssif_data[1] & 0x3;
	/* Force sequence field = 0 for SSIF */
	context->ssif_pending_msg.seq = 0;
	context->ssif_pending_msg.cmd = ssif_data[2];

	return &context->ssif_pending_msg;
}

/*
 * Send request from the SSIF driver
 */
static int send_received_message_signal(struct ssifbridged_context *context,
		struct ipmi_msg *req,
		uint8_t *data, uint8_t data_len)
{
	sd_bus_message *msg = NULL;
	int r = 0;

	/* Notify sdbus for incoming message */
	r = sd_bus_message_new_signal(context->bus,
			&msg,
			OBJ_NAME,
			DBUS_NAME,
			"ReceivedMessage");
	if (r < 0) {
		MSG_ERR("Failed to create signal: %s\n", strerror(-r));
		return r;
	}

	r = sd_bus_message_append(msg, "yyyy",
			req->seq,
			req->netfn,
			req->lun,
			req->cmd);
	if (r < 0) {
		MSG_ERR("Couldn't append to signal: %s\n", strerror(-r));
		return r;
	}

	r = sd_bus_message_append_array(msg, 'y', data, data_len);
	if (r < 0) {
		MSG_ERR("Couldn't append array to signal: %s\n", strerror(-r));
		sd_bus_message_unref(msg);
		return r;
	}

	MSG_OUT("Sending dbus signal with seq 0x%02x, netfn 0x%02x, "
			"lun 0x%02x, cmd 0x%02x\n",
			req->seq,
			req->netfn,
			req->lun,
			req->cmd);

	if (verbosity == SSIF_LOG_DEBUG) {
		int i;
		for (i = 0; i < req->data_len; i++) {
			if (i % 8 == 0) {
				if (i)
					printf("\n");
				MSG_OUT("\t");
			}
			printf("0x%02x ", data[i + 3]);
		}
		if (req->data_len)
			printf("\n");
	}

	r = sd_bus_send(context->bus, msg, NULL);
	if (r < 0) {
		MSG_ERR("Couldn't emit dbus signal: %s\n", strerror(-r));
		return r;
	}

	sd_bus_message_unref(msg);

	return r;
}

static int method_send_sms_atn(sd_bus_message *msg, void *userdata,
		sd_bus_error *ret_error)
{
	int r;
	struct ssifbridged_context *ssif_fd = userdata;

	MSG_OUT("Sending SMS_ATN ioctl (%d) to %s\n",
			SSIF_BMC_IOCTL_SMS_ATN, SSIF_BMC_PATH);

	r = ioctl(ssif_fd->fds[SSIF_FD].fd, SSIF_BMC_IOCTL_SMS_ATN);
	if (r == -1) {
		r = errno;
		MSG_ERR("Couldn't ioctl() to 0x%x, %s: %s\n",
				ssif_fd->fds[SSIF_FD].fd,
				SSIF_BMC_PATH,
				strerror(r));
		return sd_bus_reply_method_errno(msg, errno, ret_error);
	}

	r = 0;

	return sd_bus_reply_method_return(msg, "x", r);
}

/*
 * Send a response on the SSIF driver
 */
static int ssif_send_response(struct ssifbridged_context *context,
		struct ipmi_msg *ssif_resp_msg)
{
	uint8_t data[SSIF_MAX_RESP_LEN] = { 0 };
	int r = 0;
	int len = 0;

	assert(context);

	if (!ssif_resp_msg)
		return -EINVAL;

	/* netfn/lun + cmd + cc = 3 */
	data[0] = ssif_resp_msg->data_len + 3;

	/* Copy response message to data buffer */
	if (ssif_resp_msg->data_len)
		memcpy(data + 4, ssif_resp_msg->data, ssif_resp_msg->data_len);

	/* Prepare Header */
	data[1] = (ssif_resp_msg->netfn << 2) |
		(ssif_resp_msg->lun & 0x3);
	data[2] = ssif_resp_msg->cmd;
	data[3] = ssif_resp_msg->cc;
	if (ssif_resp_msg->data_len > sizeof(data) - 4) {
		MSG_ERR("Response message size (%zu) too big, truncating\n",
				ssif_resp_msg->data_len);
		ssif_resp_msg->data_len = sizeof(data) - 4;
	}

	/* Write data kernel space via system calls */
	len = write(context->fds[SSIF_FD].fd, data, data[0] + 1);

	if (len < 0) {
		MSG_ERR("Failed to write to driver (ret: %d, errno: %d)\n",
				len,
				errno);
		r = -errno;
	} else if (len != data[0] + 1) {
		MSG_ERR("Possible short write to %s, desired len: %d, "
				"written len: %d\n",
				SSIF_BMC_PATH,
				data[0] + 1,
				len);
		r = -EINVAL;
	} else {
		MSG_OUT("Successfully wrote %d of %d bytes to %s\n",
				len,
				data[0] + 1,
				SSIF_BMC_PATH);
	}

	return r;
}

static int method_send_message(sd_bus_message *msg,
		void *userdata,
		sd_bus_error *ret_error)
{
	struct ssifbridged_context *context;
	sd_bus_message* resp_msg = NULL;
	struct ipmi_msg ssif_resp_msg;
	int r = 1;

	context = (struct ssifbridged_context *)userdata;
	if (!context) {
		sd_bus_error_set_const(ret_error,
				"org.openbmc.error",
				"Internal error");
		r = -EINVAL;
		goto done;
	}

	r = sd_bus_message_new_method_return(msg, &resp_msg);
	if (r < 0) {
		MSG_ERR("Failed to create method response (ret: %d)\n", r);
		return r;
	}

	if (!context->awaiting_response) {
		/* We are not expecting a response at this time */
		MSG_ERR("Response message received when in wrong state. "
				"Discarding\n");
		r = -EBUSY;
	} else {
		uint8_t *data;
		size_t data_sz;
		uint8_t netfn, lun, seq, cmd, cc;
		struct itimerspec ts;

		context->awaiting_response = 0;

		r = sd_bus_message_read(msg, "yyyyy",
				&seq,
				&netfn,
				&lun,
				&cmd,
				&cc);
		if (r < 0) {
			MSG_ERR("Couldn't parse leading bytes of message: %s\n",
					strerror(-r));
			sd_bus_error_set_const(ret_error,
					"org.openbmc.error",
					"Bad message");
			r = -EINVAL;
			goto done;
		}
		r = sd_bus_message_read_array(msg, 'y',
				(const void **)&data,
				&data_sz);
		if (r < 0) {
			MSG_ERR("Couldn't parse data bytes of message: %s\n",
					strerror(-r));
			sd_bus_error_set_const(ret_error,
					"org.openbmc.error",
					"Bad message data");
			r = -EINVAL;
			goto done;
		}

		MSG_OUT("Received a dbus response for msg with seq 0x%02x\n",
				seq);

		ssif_resp_msg.netfn = netfn;
		ssif_resp_msg.lun = lun;
		ssif_resp_msg.seq = seq;
		ssif_resp_msg.cmd = cmd;
		ssif_resp_msg.cc = cc;
		ssif_resp_msg.data_len = data_sz;
		/* Because we've ref'ed the msg, don't need to memcpy data */
		ssif_resp_msg.data = data;

		/* Clear the timer */
		ts.it_interval.tv_sec = 0;
		ts.it_interval.tv_nsec = 0;
		ts.it_value.tv_sec = 0;
		ts.it_value.tv_nsec = 0;
		r = timerfd_settime(context->fds[TIMER_FD].fd,
				TFD_TIMER_ABSTIME,
				&ts,
				NULL);

		if (r < 0) {
			MSG_ERR("Failed to clear timer\n");
		}

		r = ssif_send_response(context, &ssif_resp_msg);
	}

done:
	r = sd_bus_message_append(resp_msg, "x", r);
	if (r < 0) {
		MSG_ERR("Failed to add result to method (ret: %d)\n", r);
	}

	r = sd_bus_send(context->bus, resp_msg, NULL);
	if (r < 0) {
		MSG_ERR("Failed to send response (ret: %d)\n", r);
	}

	return r;
}

static int dispatch_timer(struct ssifbridged_context *context)
{
	int r = 0;

	if (context->fds[TIMER_FD].revents & POLLIN) {
		if(!context->awaiting_response) {
			/* Got a timeout but not expecting a response */
			MSG_ERR("Timeout but no pending message\n");
		} else {
			struct itimerspec ts;

			/* Clear the timer */
			ts.it_interval.tv_sec = 0;
			ts.it_interval.tv_nsec = 0;
			ts.it_value.tv_sec = 0;
			ts.it_value.tv_nsec = 0;
			r = timerfd_settime(context->fds[TIMER_FD].fd,
					TFD_TIMER_ABSTIME,
					&ts,
					NULL);
			if (r < 0) {
				MSG_ERR("Failed to clear timer\n");
			}

			MSG_ERR("Timing out message\n");

			/* Add one to the netfn - response netfn is always
			 * request netfn + 1
			 */
			context->ssif_pending_msg.netfn += 1;
			context->ssif_pending_msg.cc =
				IPMI_CC_CANNOT_PROVIDE_RESP;
			context->ssif_pending_msg.data = NULL;
			context->ssif_pending_msg.data_len = 0;

			r = ssif_send_response(context,
					&context->ssif_pending_msg);
			if (r < 0) {
				MSG_ERR("Failed to send timeout message "
						"(ret: %d, errno: %d)\n",
						r,
						errno);
			}

			context->awaiting_response = 0;
		}
	}

	return r;
}

static int dispatch_sd_bus(struct ssifbridged_context *context)
{
	int r = 0;
	if (context->fds[SD_BUS_FD].revents) {
		r = sd_bus_process(context->bus, NULL);
		if (r > 0)
			MSG_OUT("Processed %d dbus events\n", r);
	}

	return r;
}

static int dispatch_ssif(struct ssifbridged_context *context)
{
	int r = 0;

	assert(context);

	if (context->fds[SSIF_FD].revents & POLLIN) {
		/* We have received data on the driver */
		struct itimerspec ts;
		struct ipmi_msg *req;
		uint8_t data[SSIF_MAX_REQ_LEN] = { 0 };

		r = read(context->fds[SSIF_FD].fd, data, sizeof(data));
		if (r < 0) {
			MSG_ERR("Couldn't read from ssif: %s\n", strerror(-r));
			return r;
		}
		if (r < data[0] + 1) {
			MSG_ERR("Short read from ssif (%d vs %d)\n",
					r,
					data[1] + 2);
			r = 0;
			return r;
		}

		/* Check if response is still awaiting */
		if (context->awaiting_response) {
			MSG_ERR("Received SSIF message while awaiting response."
					" Discarding\n");
		} else {
			/* Get SSIF request message that sent from
			 * kernel space
			 */
			req = ssif_msg_create(context, data);
			context->awaiting_response = 1;

			if (!req) {
				MSG_ERR("Can not create request\n");
				r = -ENOMEM;
				return r;
			}

			/* Set up the timer. We do this before sending
			 * the signal to avoid a race condition with
			 * the response
			 */
			ts.it_interval.tv_sec = 0;
			ts.it_interval.tv_nsec = 0;
			ts.it_value.tv_nsec = 0;
			ts.it_value.tv_sec = SSIF_BMC_TIMEOUT_SEC;
			r = timerfd_settime(context->fds[TIMER_FD].fd,
					0,
					&ts,
					NULL);
			if (r < 0)
				MSG_ERR("Failed to set timer (ret: %d, "
						"errno: %d)\n",
						r,
						errno);

			r = send_received_message_signal(context,
					req,
					data + 3,
					req->data_len);
			if (r < 0) {
				MSG_ERR("Failed to send Received Message "
						"signal (ret: %d)\n",
						r);
			}
		}
	}

	return r;
}

static void usage(const char *name)
{
	fprintf(stderr, "\
 Usage %s [--v[v] | --syslog] [-d <DEVICE>]\n\
 --v                    Be verbose\n\
 --vv                   Be verbose and dump entire messages\n\
 -s, --syslog           Log output to syslog (pointless without --verbose)\n\
 -d, --device <DEVICE>  use <DEVICE> file. Default is '%s'\n\n",
 name, ssif_bmc_device);
}

static const sd_bus_vtable ipmid_vtable[] = {
	SD_BUS_VTABLE_START(0),
	SD_BUS_METHOD("sendMessage",
			"yyyyyay",
			"x",
			&method_send_message,
			SD_BUS_VTABLE_UNPRIVILEGED),
	SD_BUS_METHOD("setAttention",
			"",
			"x",
			&method_send_sms_atn,
			SD_BUS_VTABLE_UNPRIVILEGED),
	SD_BUS_SIGNAL("ReceivedMessage",
			"yyyyay",
			0),
	SD_BUS_VTABLE_END
};

int main(int argc, char *argv[]) {
	struct ssifbridged_context *context;
	const char *name = argv[0];
	int opt, polled, r;

	static const struct option long_options[] = {
		{ "device",  required_argument, NULL, 'd' },
		{ "v",       no_argument, (int *)&verbosity, SSIF_LOG_VERBOSE },
		{ "vv",      no_argument, (int *)&verbosity, SSIF_LOG_DEBUG   },
		{ "syslog",  no_argument, 0,          's'         },
		{ 0,         0,           0,          0           }
	};

	context = calloc(1, sizeof(*context));

	ssif_vlog = &ssif_log_console;
	while ((opt = getopt_long(argc, argv, "", long_options, NULL)) != -1) {
		switch (opt) {
			case 0:
				break;
			case 'd':
				ssif_bmc_device = optarg;
				break;
			case 's':
				/* Avoid a double openlog() */
				if (ssif_vlog != &vsyslog) {
					openlog(PREFIX, LOG_ODELAY, LOG_DAEMON);
					ssif_vlog = &vsyslog;
				}
				break;
			default:
				usage(name);
				exit(EXIT_FAILURE);
		}
	}

	if (verbosity == SSIF_LOG_VERBOSE)
		MSG_OUT("Verbose logging\n");

	if (verbosity == SSIF_LOG_DEBUG)
		MSG_OUT("Debug logging\n");

	MSG_OUT("Starting\n");
	r = sd_bus_default_system(&context->bus);
	if (r < 0) {
		MSG_ERR("Failed to connect to system bus: %s\n", strerror(-r));
		goto finish;
	}

	MSG_OUT("Registering dbus methods/signals\n");
	r = sd_bus_add_object_vtable(context->bus,
			NULL,
			OBJ_NAME,
			DBUS_NAME,
			ipmid_vtable,
			context);
	if (r < 0) {
		MSG_ERR("Failed to issue method call: %s\n", strerror(-r));
		goto finish;
	}

	MSG_OUT("Requesting dbus name: %s\n", DBUS_NAME);
	r = sd_bus_request_name(context->bus, DBUS_NAME,
		SD_BUS_NAME_ALLOW_REPLACEMENT | SD_BUS_NAME_REPLACE_EXISTING);
	if (r < 0) {
		MSG_ERR("Failed to acquire service name: %s\n", strerror(-r));
		goto finish;
	}

	MSG_OUT("Getting dbus file descriptors\n");
	context->fds[SD_BUS_FD].fd = sd_bus_get_fd(context->bus);
	if (context->fds[SD_BUS_FD].fd < 0) {
		r = -errno;
		MSG_OUT("Couldn't get the bus file descriptor: %s\n",
				strerror(errno));
		goto finish;
	}

	MSG_OUT("Opening %s\n", SSIF_BMC_PATH);
	context->fds[SSIF_FD].fd = open(SSIF_BMC_PATH, O_RDWR | O_NONBLOCK);
	if (context->fds[SSIF_FD].fd < 0) {
		r = -errno;
		MSG_ERR("Couldn't open %s with flags O_RDWR: %s\n",
				SSIF_BMC_PATH,
				strerror(errno));
		goto finish;
	}

	MSG_OUT("Creating timer fd\n");
	context->fds[TIMER_FD].fd = timerfd_create(CLOCK_MONOTONIC, 0);
	if (context->fds[TIMER_FD].fd < 0) {
		r = -errno;
		MSG_ERR("Couldn't create timer fd: %s\n", strerror(errno));
		goto finish;
	}
	context->fds[SD_BUS_FD].events = POLLIN;
	context->fds[SSIF_FD].events = POLLIN;
	context->fds[TIMER_FD].events = POLLIN;

	MSG_OUT("Entering polling loop\n");

	while (running) {
		polled = poll(context->fds, TOTAL_FDS, -1);
		if (polled == 0)
			continue;
		if (polled < 0) {
			r = -errno;
			MSG_ERR("Error from poll(): %s\n",
					strerror(errno));
			goto finish;
		}
		r = dispatch_sd_bus(context);
		if (r < 0) {
			MSG_ERR("Error handling dbus event: %s\n",
					strerror(-r));
			goto finish;
		}
		r = dispatch_ssif(context);
		if (r < 0) {
			MSG_ERR("Error handling SSIF event: %s\n",
					strerror(-r));
			goto finish;
		}
		r = dispatch_timer(context);
		if (r < 0) {
			MSG_ERR("Error handling timer event: %s\n",
					strerror(-r));
			goto finish;
		}
	}

finish:
	close(context->fds[SSIF_FD].fd);
	close(context->fds[TIMER_FD].fd);
	sd_bus_unref(context->bus);
	free(context);

	return r;
}
