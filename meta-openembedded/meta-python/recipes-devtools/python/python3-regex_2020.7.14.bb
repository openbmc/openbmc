SUMMARY = "Alternative regular expression module, to replace re."
HOMEPAGE = "https://bitbucket.org/mrabarnett/mrab-regex/src"
LICENSE = "PSF"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=ee7987010dadc17745d623f406b500ec"

inherit pypi setuptools3

SRC_URI[md5sum] = "c3a53929c3797289566368be4b6b964a"
SRC_URI[sha256sum] = "3a3af27a8d23143c49a3420efe5b3f8cf1a48c6fc8bc6856b03f638abc1833bb"

BBCLASSEXTEND = "native nativesdk"
