SUMMARRY = "spawn-fcgi is used to spawn FastCGI applications"
HOMEPAGE = "http://redmine.lighttpd.net/projects/spawn-fcgi"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SRC_URI = "http://download.lighttpd.net/spawn-fcgi/releases-1.6.x/spawn-fcgi-${PV}.tar.gz \
           file://fix_configure_ipv6_test.patch"

SRC_URI[md5sum] = "e970de4efe8045c01dd76280f39901aa"
SRC_URI[sha256sum] = "ab327462cb99894a3699f874425a421d934f957cb24221f00bb888108d9dd09e"

inherit autotools

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "ipv6", "ipv6", "", d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
