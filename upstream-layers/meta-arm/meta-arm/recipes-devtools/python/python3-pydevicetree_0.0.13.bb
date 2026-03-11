SUMMARY = "Python 3 library for parsing, querying, and modifying Devicetree Source v1 files"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=2862203650706153a696787039ebd509"
HOMEPAGE = "https://github.com/sifive/pydevicetree"

inherit pypi setuptools3

SRC_URI[sha256sum] = "5700c05df89bad8fd729c11aa6f764a3323bcb3796f13b32481ae34445cfc1b7"

BBCLASSEXTEND = "native nativesdk"
