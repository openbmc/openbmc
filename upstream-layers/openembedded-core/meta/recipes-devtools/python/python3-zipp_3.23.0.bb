SUMMARY = "Backport of pathlib-compatible object wrapper for zip files"
HOMEPAGE = "https://github.com/jaraco/zipp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1aeae65f25a15b1e46d4381f2f094e0a"

SRC_URI[sha256sum] = "a07157588a12518c9d4034df3fbbee09c814741a33ff63c05fa29d26a2404166"

DEPENDS += "python3-setuptools-scm-native python3-coherent-licensed-native"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-compression \
                   python3-math"

BBCLASSEXTEND = "native nativesdk"
