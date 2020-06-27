LINUX_VERSION ?= "5.4.47"
LINUX_RPI_BRANCH ?= "rpi-5.4.y"

SRCREV = "dec0ddc506ab5d93a7de4b8a7c8dc98e0a96f85c"

require linux-raspberrypi_5.4.inc

SRC_URI += "file://0001-Revert-selftests-bpf-Skip-perf-hw-events-test-if-the.patch \
            file://0002-Revert-selftests-bpf-Fix-perf_buffer-test-on-systems.patch \
            file://powersave.cfg \
           "
