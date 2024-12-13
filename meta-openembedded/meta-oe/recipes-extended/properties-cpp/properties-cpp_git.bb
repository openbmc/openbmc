# Copyright (c) 2019 Christophe Chapuis <chris.chapuis@gmail.com>
# Copyright (c) 2019 Herman van Hazendonk <github.com@herrie.org>

SUMMARY = "A very simple convenience library for handling properties and signals in C++11."
SECTION = "libs"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

PV = "0.0.1+git"

SRCREV = "45863e849b39c4921d6553e6d27e267a96ac7d77"
SRC_URI = "git://github.com/lib-cpp/${BPN}.git;branch=master;protocol=https"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

do_configure:prepend() {
    echo " " > ${S}/tests/CMakeLists.txt
}

inherit cmake pkgconfig

RDEPENDS:${PN}-dev = ""
