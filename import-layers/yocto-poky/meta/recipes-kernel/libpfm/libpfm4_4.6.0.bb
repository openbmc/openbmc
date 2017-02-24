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

SRC_URI = "http://downloads.sourceforge.net/project/perfmon2/${BPN}/libpfm-${PV}.tar.gz \
           file://0001-Makefile-Add-LDFLAGS-variable-to-SLDFLAGS.patch \
           file://fix-misleading-indentation-error.patch \
          "

SRC_URI[md5sum] = "5077b9022440e4951d96f2d0e73bd487"
SRC_URI[sha256sum] = "5ab1e5b0472550f9037a8800834f6bc3b927690070f69fac0b67284b4b05fd5f"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/perfmon2/files/libpfm4/"

EXTRA_OEMAKE = "DESTDIR=\"${D}\" PREFIX=\"${prefix}\" LIBDIR=\"${libdir}\" LDCONFIG=\"true\""
EXTRA_OEMAKE_append_powerpc = " ARCH=\"powerpc\""
EXTRA_OEMAKE_append_powerpc64 = " ARCH=\"powerpc\" BITMODE=\"64\""

S = "${WORKDIR}/libpfm-${PV}"

do_install () {
	oe_runmake install
}
