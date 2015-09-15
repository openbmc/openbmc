# Copyright (C) 2013 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Library of utility functions from BSD systems"
DESCRIPTION = "This library provides useful functions commonly found on BSD systems, \
               and lacking on others like GNU systems, thus making it easier to port \
               projects with strong BSD origins, without needing to embed the same \
               code over and over again on each project."

HOMEPAGE = "http://libbsd.freedesktop.org/wiki/"
LICENSE = "BSD-4-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f1530ea92aeaa1c5e2547cfd43905d8c"
SECTION = "libs"
DEPENDS = ""

SRC_URI = "http://libbsd.freedesktop.org/releases/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "fcceb4e66fd448ca4ed42ba22a8babb0"
SRC_URI[sha256sum] = "0f3b0e17e5c34c038126e0a04351b11e23c6101a7d0ce3beeab29bb6415c10bb"

inherit autotools pkgconfig
