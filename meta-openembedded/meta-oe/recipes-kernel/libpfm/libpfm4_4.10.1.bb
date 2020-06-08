SUMMARY = "Monitoring tools exploiting the performance monitoring events"
DESCRIPTION = "This package provides a library, called libpfm4 which is used to develop \
monitoring tools exploiting the performance monitoring events such as those \
provided by the Performance Monitoring Unit (PMU) of modern processors."
HOMEPAGE = "http://perfmon2.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=144822&atid=759953&source=navbar"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0de488f3bd4424e308e2e399cb99c788"

SECTION = "devel"

COMPATIBLE_HOST = "powerpc64"

SRC_URI = "${SOURCEFORGE_MIRROR}/perfmon2/${BPN}/libpfm-${PV}.tar.gz \
           file://0001-Include-poll.h-instead-of-sys-poll.h.patch \
          "
SRC_URI[md5sum] = "d8f66cb9bfa7e1434434e0de6409db5b"
SRC_URI[sha256sum] = "c61c575378b5c17ccfc5806761e4038828610de76e2e34fac9f7fa73ba844b49"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/perfmon2/files/libpfm4/"

EXTRA_OEMAKE = "DESTDIR=\"${D}\" PREFIX=\"${prefix}\" LIBDIR=\"${libdir}\" LDCONFIG=\"true\""
EXTRA_OEMAKE_append_powerpc = " ARCH=\"powerpc\""
EXTRA_OEMAKE_append_powerpc64 = " ARCH=\"powerpc\" BITMODE=\"64\""
EXTRA_OEMAKE_append_powerpc64le = " ARCH=\"powerpc\" BITMODE=\"64\""

S = "${WORKDIR}/libpfm-${PV}"

do_install () {
	oe_runmake install
}
