SUMMARY = "spawn-fcgi is used to spawn FastCGI applications"
HOMEPAGE = "http://redmine.lighttpd.net/projects/spawn-fcgi"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4dac5c6ab169aa212feb5028853a579"

SRC_URI = "http://download.lighttpd.net/spawn-fcgi/releases-1.6.x/spawn-fcgi-${PV}.tar.gz"

SRC_URI[sha256sum] = "4ffe2e9763cf71ca52c3d642a7bfe20d6be292ba0f2ec07a5900c3110d0e5a85"

inherit meson

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "-Dipv6=true,-Dipv6=false,"
