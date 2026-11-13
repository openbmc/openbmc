SUMMARY = "Python extension for MurmurHash (MurmurHash3), a set of fast and \
           robust hash functions"
HOMEPAGE = "https://github.com/hajimes/mmh3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=53366c60f8214cfc1d3622ebacd141fb"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "bd86d0c86b52332319d981d03781ff77811a29db544a69902dc06b5506bb3e19"

BBCLASSEXTEND = "native nativesdk"
