#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <systemd/sd-bus.h>

static int method_echo(sd_bus_message *m, void *userdata, sd_bus_error *ret_error) {
	char *str;
	const char *intf = sd_bus_message_get_interface(m),
	      *path = sd_bus_message_get_path(m);
	sd_bus *bus = sd_bus_message_get_bus(m);

	char response[512] = {0};
	int r;

	/* Read the parameters */
	r = sd_bus_message_read(m, "s", &str);
	if (r < 0) {
		fprintf(stderr, "Failed to parse parameters: %s\n", strerror(-r));
		return r;
	}

	r = sd_bus_emit_signal(bus, path, intf, "MethodInvoked", "ss",
			"Echo method was invoked", path);
	if (r < 0) {
		fprintf(stderr, "Failed to emit signal: %s\n", strerror(-r));
		return r;
	}

	strncat(response, path, 128);
	strcat(response, " says ");
	strncat(response, str, 128);

	/* Reply with the response */
	return sd_bus_reply_method_return(m, "s", &response);
}

static const sd_bus_vtable echo_vtable[] = {
	SD_BUS_VTABLE_START(0),
	SD_BUS_METHOD("Echo", "s", "s", method_echo, SD_BUS_VTABLE_UNPRIVILEGED),
	SD_BUS_SIGNAL("MethodInvoked", "s", 0),
	SD_BUS_VTABLE_END
};

int main(int argc, char *argv[]) {
	sd_bus_slot *slot = NULL;
	sd_bus *bus = NULL;
	int r;
	char **acquired = NULL, **activatable = NULL, **i;

	/* Connect to the user bus this time */
	r = sd_bus_open_system(&bus);
	if (r < 0) {
		fprintf(stderr, "Failed to connect to system bus: %s\n", strerror(-r));
		goto finish;
	}

	/* Install an object */
	r = sd_bus_add_object_vtable(bus,
			&slot,
			"/org/openbmc/examples/path0/SDBusObj",  /* object path */
			"org.openbmc.examples.Echo",   /* interface name */
			echo_vtable,
			NULL);
	if (r < 0) {
		fprintf(stderr, "Failed to issue method call: %s\n", strerror(-r));
		goto finish;
	}

	/* Install an object */
	r = sd_bus_add_object_vtable(bus,
			&slot,
			"/org/openbmc/examples/path1/SDBusObj",  /* object path */
			"org.openbmc.examples.Echo",   /* interface name */
			echo_vtable,
			NULL);
	if (r < 0) {
		fprintf(stderr, "Failed to issue method call: %s\n", strerror(-r));
		goto finish;
	}

	/* Take a well-known service name so that clients can find us */
	r = sd_bus_request_name(bus, "org.openbmc.examples.SDBusService", 0);
	if (r < 0) {
		fprintf(stderr, "Failed to acquire service name: %s\n", strerror(-r));
		goto finish;
	}

	for (;;) {
		/* Process requests */
		r = sd_bus_process(bus, NULL);
		if (r < 0) {
			fprintf(stderr, "Failed to process bus: %s\n", strerror(-r));
			goto finish;
		}
		if (r > 0) /* we processed a request, try to process another one, right-away */
			continue;

		/* Wait for the next request to process */
		r = sd_bus_wait(bus, (uint64_t) -1);
		if (r < 0) {
			fprintf(stderr, "Failed to wait on bus: %s\n", strerror(-r));
			goto finish;
		}
	}

finish:
	sd_bus_slot_unref(slot);
	sd_bus_unref(bus);

	return r < 0 ? EXIT_FAILURE : EXIT_SUCCESS;
}
