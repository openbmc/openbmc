SUMMARY = "Easy progress reporting for Python"
HOMEPAGE = "https://github.com/verigak/progress/"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=00ab78a4113b09aacf63d762a7bb9644"

SRC_URI[sha256sum] = "c1ba719f862ce885232a759eab47971fe74dfc7bb76ab8a51ef5940bad35086c"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN}:class-target += " \
    python3-datetime \
    python3-math \
"

BBCLASSEXTEND = "native nativesdk"
