SUMMARY = "Python extension for MurmurHash (MurmurHash3), a set of fast and \
           robust hash functions"
HOMEPAGE = "https://github.com/hajimes/mmh3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=53366c60f8214cfc1d3622ebacd141fb"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "95832419b87b882bec9dcd7d041d74887ba7745b3659c14be1ae1db5cfa35cad"

BBCLASSEXTEND = "native nativesdk"
