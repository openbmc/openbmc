# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Python library for CSON (schema-compressed JSON)"
HOMEPAGE = "https://github.com/gt3389b/python-cson/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7709d2635e63ab96973055a23c2a4cac"

SRCREV = "f3f2898c44bb16b951d3e9f2fbf6d1c4158edda2"
SRC_URI = "git://github.com/gt3389b/python-cson.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

RDEPENDS_${PN}_class-native = ""
DEPENDS_append_class-native = " python-native "

inherit setuptools3

BBCLASSEXTEND = "native"

