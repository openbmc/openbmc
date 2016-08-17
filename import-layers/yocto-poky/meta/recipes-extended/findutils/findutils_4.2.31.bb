LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
PR = "r4"

require findutils.inc

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://gnulib-extension.patch \
           file://findutils_fix_for_automake-1.12.patch \
           file://findutils-fix-doc-build-error.patch \
           "

SRC_URI[md5sum] = "a0e31a0f18a49709bf5a449867c8049a"
SRC_URI[sha256sum] = "e0d34b8faca0b3cca0703f6c6b498afbe72f0ba16c35980c10ec9ef7724d6204"
