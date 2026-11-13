SUMMARY = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e21c3f75eb8d9427c8a611a8e83e9d6"

SRC_URI[sha256sum] = "cf474b90b07ea932637e17c6b9479de2b2882900b0d311bdaf4f5c000a15a97f"

DEPENDS += "python3-setuptools-scm-native python3-coherent-licensed-native"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-compression \
                   python3-math"

BBCLASSEXTEND = "native nativesdk"
