SUMMARY = "Fast, Extensible Progress Meter"
HOMEPAGE = "http://tqdm.github.io/"
SECTION = "devel/python"

LICENSE = "MIT & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=054ef7105cba050017d1368262a1623c"

inherit pypi

SRC_URI[md5sum] = "9a092a5878090996bbb0ed92bf572a8a"
SRC_URI[sha256sum] = "c1d677f3a85fa291b34bdf8f770f877119b9754b32673699653556f85e2c2f13"

BBCLASSEXTEND = "native nativesdk"
