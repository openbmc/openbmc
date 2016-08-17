SUMMARY = "Babeltrace - Trace Format Babel Tower"
DESCRIPTION = "Babeltrace provides trace read and write libraries in host side, as well as a trace converter, which used to convert LTTng 2.0 traces into human-readable log."
HOMEPAGE = "http://www.efficios.com/babeltrace/"
BUGTRACKER = "https://bugs.lttng.org/projects/babeltrace"

LICENSE = "MIT & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76ba15dd76a248e1dd526bca0e2125fa"

DEPENDS = "glib-2.0 util-linux popt bison-native flex-native"

inherit autotools pkgconfig

SRCREV = "6e2b9096c77138593097335b9b27202d98918089"

SRC_URI = "git://git.efficios.com/babeltrace.git;branch=stable-1.3 \
           file://0001-Fix-invalid-pointer-free-with-trace-collection.patch \
           file://0001-lttng-live-Include-sys-param.h-for-MAXNAMLEN-definti.patch \
"

S = "${WORKDIR}/git"

do_configure_prepend () {
	( cd ${S}; ${S}/bootstrap )
}
