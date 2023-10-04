SUMMARY = "ACPI data gathering library"
DESCRIPTION = "General purpose shared library for programs gathering ACPI data on Linux. \
Thermal zones, battery infomration, fan information and AC states are implemented."
SECTION = "base"
HOMEPAGE = "http://www.ngolde.de/libacpi.html"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fec17f82f16630adf2dfb7d2a46f21c5"

SRC_URI = "http://www.ngolde.de/download/libacpi-${PV}.tar.gz \
	   file://makefile-fix.patch \
	   file://libacpi_fix_for_x32.patch \
	   file://use_correct_strip_in_cross_environment.patch \
	   file://ldflags.patch \
           file://0001-libacpi-Fix-build-witth-fno-commom.patch \
           "

SRC_URI[md5sum] = "05b53dd7bead66dda35fec502b91066c"
SRC_URI[sha256sum] = "13086e31d428b9c125954d48ac497b754bbbce2ef34ea29ecd903e82e25bad29"

UPSTREAM_CHECK_URI = "http://www.ngolde.de/libacpi.html"

inherit lib_package

COMPATIBLE_HOST = '(x86_64|i.86|aarch64).*-(linux|freebsd.*)'

CFLAGS += "-fPIC"
EXTRA_OEMAKE = '-e MAKEFLAGS= STRIP="echo"'

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
	oe_runmake install DESTDIR=${D} PREFIX=${exec_prefix} LIBDIR=${libdir}
}
