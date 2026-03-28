SUMMARY = "Python extension for MurmurHash (MurmurHash3), a set of fast and \
           robust hash functions"
HOMEPAGE = "https://github.com/hajimes/mmh3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=53366c60f8214cfc1d3622ebacd141fb"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "bbea5b775f0ac84945191fb83f845a6fd9a21a03ea7f2e187defac7e401616ad"

BBCLASSEXTEND = "native nativesdk"
