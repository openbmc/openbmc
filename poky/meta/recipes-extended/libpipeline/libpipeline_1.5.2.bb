SUMMARY = "pipeline manipulation library"
DESCRIPTION = "This is a C library for setting up and running pipelines of processes, \
without needing to involve shell command-line parsing which is often \
error-prone and insecure."
HOMEPAGE = "http://libpipeline.nongnu.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/libpipeline/libpipeline-${PV}.tar.gz"
SRC_URI[md5sum] = "169de4cc1f6f7f7d430a5bed858b2fd3"
SRC_URI[sha256sum] = "fd59c649c1ae9d67604d1644f116ad4d297eaa66f838e3dfab96b41e85b059fb"

inherit pkgconfig autotools

acpaths = "-I ${S}/gl/m4 -I ${S}/m4"
