SUMMARY = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2e21c3f75eb8d9427c8a611a8e83e9d6"

SRC_URI[sha256sum] = "32120e378d32cd9714ad503c1d024619063ec28aad2248dc6672ad13edfa5110"

DEPENDS += "python3-setuptools-scm-native python3-coherent-licensed-native"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-compression \
                   python3-math"

BBCLASSEXTEND = "native nativesdk"
