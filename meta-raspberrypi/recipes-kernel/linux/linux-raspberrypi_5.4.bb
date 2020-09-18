LINUX_VERSION ?= "5.4.64"
LINUX_RPI_BRANCH ?= "rpi-5.4.y"

SRCREV = "65caf603f3b1c43f4c92939f7fbb7149e054f486"

require linux-raspberrypi_5.4.inc

SRC_URI += "file://0001-Revert-selftests-bpf-Skip-perf-hw-events-test-if-the.patch \
            file://0002-Revert-selftests-bpf-Fix-perf_buffer-test-on-systems.patch \
            file://0001-perf-cs-etm-Move-definition-of-traceid_list-global-v.patch \
            file://powersave.cfg \
            file://android-drivers.cfg \
           "
