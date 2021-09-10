SUMMARY = "pipeline manipulation library"
DESCRIPTION = "This is a C library for setting up and running pipelines of processes, \
without needing to involve shell command-line parsing which is often \
error-prone and insecure."
HOMEPAGE = "http://libpipeline.nongnu.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/libpipeline/libpipeline-${PV}.tar.gz"
SRC_URI[sha256sum] = "5dbf08faf50fad853754293e57fd4e6c69bb8e486f176596d682c67e02a0adb0"

inherit pkgconfig autotools

acpaths = "-I ${S}/gl/m4 -I ${S}/m4"
