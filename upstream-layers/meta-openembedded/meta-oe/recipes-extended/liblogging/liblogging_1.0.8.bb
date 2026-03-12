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
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=63fe03535d83726f5655072502bef1bc"
SRC_URI = "http://download.rsyslog.com/${BPN}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "6449b7bb75dc282ec6bf1b98a753c950746ea5b190ec9aee097881e4dc5c4bf1"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-man-pages"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--enable-journal, --disable-journal, systemd"
