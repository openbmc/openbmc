SUMMARY = "pipeline manipulation library"
DESCRIPTION = "This is a C library for setting up and running pipelines of processes, \
without needing to involve shell command-line parsing which is often \
error-prone and insecure."
HOMEPAGE = "http://libpipeline.nongnu.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/libpipeline/libpipeline-${PV}.tar.gz"
SRC_URI[md5sum] = "b7437a5020190cfa84f09c412db38902"
SRC_URI[sha256sum] = "0d72e12e4f2afff67fd7b9df0a24d7ba42b5a7c9211ac5b3dcccc5cd8b286f2b"

inherit pkgconfig autotools

