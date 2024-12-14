SUMMARY = "Example DirectFB applications"
DESCRIPTION = "The DirectFB-examples package contains a set of simple DirectFB \
      applications that can be used to test and demonstrate various DirectFB \
      features"
DEPENDS = "directfb"
SECTION = "libs"
LICENSE = "MIT"

SRC_URI = " \
           http://downloads.yoctoproject.org/mirror/sources/DirectFB-examples-${PV}.tar.gz \
           file://configure.in-Fix-string-argument-syntax.patch \
           file://0001-spacedream-Add-typecast-to-pthread_t-in-assignment.patch \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=ecf6fd2b19915afc4da56043926ca18f"

S = "${WORKDIR}/DirectFB-examples-${PV}"

inherit autotools pkgconfig

SRC_URI[sha256sum] = "9a2104cc4da8123c8371813551b66b943198979f745cbebc034bb5e10844122a"
