LINUX_VERSION ?= "4.19.126"
LINUX_RPI_BRANCH ?= "rpi-4.19.y"

SRCREV = "f6b3ac28f0a9137d4c24c0b8832e693bbd16f5b7"

require linux-raspberrypi_4.19.inc

SRC_URI += "file://0001-perf-Make-perf-able-to-build-with-latest-libbfd.patch \
            file://0001-selftest-bpf-Use-CHECK-macro-instead-of-RET_IF.patch \
           "
