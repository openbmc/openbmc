# Copyright (C) 2021 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "pytest plugin with mechanisms for caching across test runs"
HOMEPAGE = "http://bitbucket.org/hpk42/pytest-cache/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6bb0320b04a0a503f12f69fea479de9"

SRC_URI[sha256sum] = "be7468edd4d3d83f1e844959fd6e3fd28e77a481440a7118d430130ea31b07a9"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-core python3-execnet python3-pprint python3-py python3-pytest"

BBCLASSEXTEND = "native nativesdk"
