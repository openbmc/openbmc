SUMMARY = "A small tool to read/write memory"
DESCRIPTION = "rwmem is a small tool for reading and writing device registers. \
rwmem supports two modes: mmap mode and i2c mode. \
\
In mmap mode rwmem accesses a file by memory mapping it. \
Using /dev/mem as the memory mapped file makes rwmem access memory and \
can thus be used to access devices which have memory mapped registers. \
\
In i2c mode rwmem accesses an i2c peripheral by sending i2c messages to it."

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "fmt libinih"

PV .= "+git${SRCPV}"

SRCREV = "8416326777b2aada0706539b8f9f6acefa476b16"

SRC_URI = "git://github.com/tomba/rwmem.git;protocol=https;name=rwmem;branch=master \
           file://0001-include-missing-cstdint.patch"

S = "${WORKDIR}/git"

inherit meson pkgconfig python3native

PACKAGECONFIG ?= "python static"
PACKAGECONFIG[python] = "-Dpyrwmem=enabled,-Dpyrwmem=disabled,cmake-native python3 python3-pybind11"
PACKAGECONFIG[static] = "-Dstatic-libc=true,-Dstatic-libc=false,"

do_install:append() {
	install -D -m 0644 ${B}/librwmem/librwmem.a ${D}${libdir}/librwmem.a
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}/pyrwmem"
