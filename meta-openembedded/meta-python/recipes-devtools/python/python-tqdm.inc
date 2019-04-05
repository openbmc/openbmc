SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=054ef7105cba050017d1368262a1623c"

inherit pypi

SRC_URI[md5sum] = "807e8c13e16e3660ac88e05d711daeb0"
SRC_URI[sha256sum] = "e22977e3ebe961f72362f6ddfb9197cc531c9737aaf5f607ef09740c849ecd05"

BBCLASSEXTEND = "native nativesdk"
