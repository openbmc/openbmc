LINUX_VERSION ?= "5.4.50"
LINUX_RPI_BRANCH ?= "rpi-5.4.y"

SRCREV = "856e83151cf3f802c495585ac176bb135a08030f"

require linux-raspberrypi_5.4.inc

SRC_URI += "file://0001-Revert-selftests-bpf-Skip-perf-hw-events-test-if-the.patch \
            file://0002-Revert-selftests-bpf-Fix-perf_buffer-test-on-systems.patch \
            file://powersave.cfg \
           "
