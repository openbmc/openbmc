SUMMARY = "Recipe with a fixed delay task"
DESCRIPTION = "Contains a delay task to be used to for testing."
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"
EXCLUDE_FROM_WORLD = "1"

do_delay() {
	sleep 5
}
do_delay[nostamp] = "1"
addtask delay