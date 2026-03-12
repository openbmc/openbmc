SUMMARY = "A small tool to read/write memory"
DESCRIPTION = "rwmem is a small tool for reading and writing device registers. \
rwmem supports two modes: mmap mode and i2c mode. \
\
In mmap mode rwmem accesses a file by memory mapping it. \
Using /dev/mem as the memory mapped file makes rwmem access memory and \
can thus be used to access devices which have memory mapped registers. \
\
In i2c mode rwmem accesses an i2c peripheral by sending i2c messages to it."
HOMEPAGE = "https://github.com/tomba/rwmem"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS += "fmt libinih"

SRCREV = "d8dda76a0d8bc3356120d087f783d237602e0278"

SRC_URI = "git://github.com/tomba/rwmem.git;protocol=https;name=rwmem;branch=master;tag=${PV} \
           file://0001-test_data_generator-Use-own-generic-htobe-template.patch \
		  "

inherit meson pkgconfig

do_install:append() {
	install -D -m 0644 ${B}/librwmem/librwmem.a ${D}${libdir}/librwmem.a
}
