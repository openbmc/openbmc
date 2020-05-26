LINUX_VERSION ?= "4.19.120"
LINUX_RPI_BRANCH ?= "rpi-4.19.y"

SRCREV = "9da67d7329873623bd5c13fae5835d76d5be8806"

require linux-raspberrypi_4.19.inc

SRC_URI += "file://0001-perf-Make-perf-able-to-build-with-latest-libbfd.patch \
            file://0001-selftest-bpf-Use-CHECK-macro-instead-of-RET_IF.patch \
           "
