SUMMARY = "Babeltrace - Trace Format Babel Tower"
DESCRIPTION = "Babeltrace provides trace read and write libraries in host side, as well as a trace converter, which used to convert LTTng 2.0 traces into human-readable log."
HOMEPAGE = "http://www.efficios.com/babeltrace/"
BUGTRACKER = "https://bugs.lttng.org/projects/babeltrace"

LICENSE = "MIT & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76ba15dd76a248e1dd526bca0e2125fa"

DEPENDS = "glib-2.0 util-linux popt bison-native flex-native"

inherit autotools pkgconfig

SRC_URI = "http://www.efficios.com/files/babeltrace/babeltrace-${PV}.tar.bz2 \
"

EXTRA_OECONF = "--disable-debug-info"

SRC_URI[md5sum] = "3e8cdafec3ac0346a389870e87bf1344"
SRC_URI[sha256sum] = "9643039923a0abc75a25b3d594cee0017423b57f10d2b625e96ed1e8d4891fc1"
