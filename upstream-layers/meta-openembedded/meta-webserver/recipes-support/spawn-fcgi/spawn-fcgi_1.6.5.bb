SUMMARY = "spawn-fcgi is used to spawn FastCGI applications"
HOMEPAGE = "http://redmine.lighttpd.net/projects/spawn-fcgi"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SRC_URI = "http://download.lighttpd.net/spawn-fcgi/releases-1.6.x/spawn-fcgi-${PV}.tar.gz"

SRC_URI[sha256sum] = "a72d7bf7fb6d1a0acda89c93d4f060bf77a2dba97ddcfecd00f11e708f592c40"

inherit autotools

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
