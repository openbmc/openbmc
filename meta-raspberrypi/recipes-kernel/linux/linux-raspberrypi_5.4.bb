LINUX_VERSION ?= "5.4.58"
LINUX_RPI_BRANCH ?= "rpi-5.4.y"

SRCREV = "4592a094787fa6a2ac1e95e96abfe4d7124dbb3a"

require linux-raspberrypi_5.4.inc

SRC_URI += "file://0001-Revert-selftests-bpf-Skip-perf-hw-events-test-if-the.patch \
            file://0002-Revert-selftests-bpf-Fix-perf_buffer-test-on-systems.patch \
            file://powersave.cfg \
           "
