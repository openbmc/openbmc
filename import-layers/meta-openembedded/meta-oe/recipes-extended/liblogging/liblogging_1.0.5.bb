SUMMARY = "An easy to use logging library"
DESCRIPTION = " \
liblogging (the upstream project) is a collection of several components. \
Namely: stdlog, journalemu, rfc3195. \
The stdlog component of liblogging can be viewed as an enhanced version of \
the syslog(3) API. It retains the easy semantics, but makes the API more \
sophisticated "behind the scenes" with better support for multiple threads \
and flexibility for different log destinations (e.g. syslog and systemd \
journal)."
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=63fe03535d83726f5655072502bef1bc"
SRC_URI = "http://download.rsyslog.com/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "44b8ce2daa1bfb84c9feaf42f9925fd7"
SRC_URI[sha256sum] = "310dc1691279b7a669d383581fe4b0babdc7bf75c9b54a24e51e60428624890b"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-man-pages"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd", "", d)}"
PACKAGECONFIG[systemd] = "--enable-journal, --disable-journal, systemd"
