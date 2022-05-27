SUMMARY = "pipeline manipulation library"
DESCRIPTION = "This is a C library for setting up and running pipelines of processes, \
without needing to involve shell command-line parsing which is often \
error-prone and insecure."
HOMEPAGE = "http://libpipeline.nongnu.org/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/libpipeline/libpipeline-${PV}.tar.gz"
SRC_URI[sha256sum] = "60fbb9e7dc398528e5f3a776af57bb28ca3fe5d9f0cd8a961ac6cebfe6e9b797"

inherit pkgconfig autotools

acpaths = "-I ${S}/gl/m4 -I ${S}/m4"
