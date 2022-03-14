SUMMARY = "A small tool to read/write memory"
DESCRIPTION = "rwmem is a small tool for reading and writing device registers. \
rwmem supports two modes: mmap mode and i2c mode. \
\
In mmap mode rwmem accesses a file by memory mapping it. \
Using /dev/mem as the memory mapped file makes rwmem access memory and \
can thus be used to access devices which have memory mapped registers. \
\
In i2c mode rwmem accesses an i2c peripheral by sending i2c messages to it."

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "python3 python3-pybind11"

PV .= "+git${SRCPV}"

SRCREV_rwmem = "3ec3e421211b58e766651c2e3a3a21acf14a1906"
SRCREV_inih = "4b10c654051a86556dfdb634c891b6c3224c4109"

SRCREV_FORMAT = "rwmem_inih"

SRC_URI = " \
    git://github.com/tomba/rwmem.git;protocol=https;name=rwmem;branch=master \
    git://github.com/benhoyt/inih.git;protocol=https;name=inih;nobranch=1;destsuffix=git/ext/inih \
"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

do_install() {
	install -D -m 0755 ${B}/bin/rwmem ${D}${bindir}/rwmem
	install -D -m 0644 ${B}/lib/librwmem.a ${D}${libdir}/librwmem.a
}
