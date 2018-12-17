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

SRC_URI[md5sum] = "f215c7e7ac6cfd1f5dabdba08c522b29"
SRC_URI[sha256sum] = "338c6174e5c8652eaa34f956be3451f7491a4416ab489aef63151f802b00bf93"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-man-pages"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--enable-journal, --disable-journal, systemd"
