SUMMARY = "Monitoring tools exploiting the performance monitoring events"
DESCRIPTION = "This package provides a library, called libpfm4 which is used to develop \
monitoring tools exploiting the performance monitoring events such as those \
provided by the Performance Monitoring Unit (PMU) of modern processors."
HOMEPAGE = "http://perfmon2.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=144822&atid=759953&source=navbar"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de488f3bd4424e308e2e399cb99c788"

SECTION = "devel"

COMPATIBLE_HOST = "powerpc64|aarch64"

SRC_URI = "${SOURCEFORGE_MIRROR}/perfmon2/${BPN}/libpfm-${PV}.tar.gz \
           file://0001-Include-poll.h-instead-of-sys-poll.h.patch \
           file://0002-perf_examples-Remove-unused-sum-variable.patch \
           "
SRC_URI[sha256sum] = "d18b97764c755528c1051d376e33545d0eb60c6ebf85680436813fa5b04cc3d1"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/perfmon2/files/libpfm4/"

EXTRA_OEMAKE = "DESTDIR=\"${D}\" PREFIX=\"${prefix}\" LIBDIR=\"${libdir}\" LDCONFIG=\"true\" DBG='-g -Wall -Wextra -Wno-unused-parameter'"
EXTRA_OEMAKE:append:powerpc = " ARCH=\"powerpc\""
EXTRA_OEMAKE:append:powerpc64 = " ARCH=\"powerpc\" BITMODE=\"64\""
EXTRA_OEMAKE:append:powerpc64le = " ARCH=\"powerpc\" BITMODE=\"64\""
EXTRA_OEMAKE:append:aarch64 = " ARCH=\"arm64\""

S = "${WORKDIR}/libpfm-${PV}"

do_install () {
	oe_runmake install
}
