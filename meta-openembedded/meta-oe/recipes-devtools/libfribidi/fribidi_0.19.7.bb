SUMMARY = "Fribidi library for bidirectional text"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=a916467b91076e631dd8edb7424769c7"
BBCLASSEXTEND = "native"

PROVIDES = "libfribidi"
DEPENDS = "glib-2.0 libpcre"

inherit autotools lib_package pkgconfig

CFLAGS_append = "  -DPAGE_SIZE=4096 "
SECURITY_CFLAGS_remove_toolchain-clang = "-D_FORTIFY_SOURCE=2"

SRC_URI = "http://fribidi.org/download/fribidi-${PV}.tar.bz2"

SRC_URI[md5sum] = "6c7e7cfdd39c908f7ac619351c1c5c23"
SRC_URI[sha256sum] = "08222a6212bbc2276a2d55c3bf370109ae4a35b689acbc66571ad2a670595a8e"

CVE_PRODUCT = "gnu_fribidi"
