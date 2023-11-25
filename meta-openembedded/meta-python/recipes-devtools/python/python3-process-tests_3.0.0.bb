# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Tools for testing processes."
HOMEPAGE = "https://github.com/ionelmc/python-process-tests"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aea36d49429f53e06868d87cd9d62349"

SRC_URI[sha256sum] = "e5d57dea7161251e91cadb84bf3ecc85275fb121fd478e579f800777b1d424bd"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += " \
    python3-io \
    python3-logging \
    python3-unittest \
"
