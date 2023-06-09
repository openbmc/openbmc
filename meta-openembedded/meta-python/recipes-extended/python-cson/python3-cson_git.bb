# Copyright (C) 2015 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Python library for CSON (schema-compressed JSON)"
HOMEPAGE = "https://github.com/gt3389b/python-cson/"
LICENSE = "MIT"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7709d2635e63ab96973055a23c2a4cac"

PV = "1.0.9+1.0.10"
SRCREV = "69090778bccc5ed124342ba288597fbb2bfa9f39"
SRC_URI = "git://github.com/gt3389b/python-cson.git;branch=master;protocol=https \
           file://0001-setup.py-Do-not-poke-at-git-describe-to-find-version.patch"

S = "${WORKDIR}/git"

RDEPENDS:${PN} = "python3-json"

inherit setuptools3

PIP_INSTALL_PACKAGE = "python_cson"

do_configure:prepend() {
    echo "__version__=${PV}" > ${S}/version.py
}

BBCLASSEXTEND = "native"

