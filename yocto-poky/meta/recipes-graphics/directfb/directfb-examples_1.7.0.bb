SUMMARY = "Example DirectFB applications"
DESCRIPTION = "The DirectFB-examples package contains a set of simple DirectFB \
      applications that can be used to test and demonstrate various DirectFB \
      features"
DEPENDS = "directfb"
SECTION = "libs"
LICENSE = "MIT"

SRC_URI = " \
           http://www.directfb.org/downloads/Extras/DirectFB-examples-${PV}.tar.gz \
           file://configure.in-Fix-string-argument-syntax.patch \
          "

LIC_FILES_CHKSUM = "file://COPYING;md5=ecf6fd2b19915afc4da56043926ca18f"

S = "${WORKDIR}/DirectFB-examples-${PV}"

inherit autotools pkgconfig

SRC_URI[md5sum] = "8b60c867af295481c32a8c7fc5802307"
SRC_URI[sha256sum] = "9a2104cc4da8123c8371813551b66b943198979f745cbebc034bb5e10844122a"
