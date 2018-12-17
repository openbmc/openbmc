SUMMARY = "A backport of traceback to older supported Pythons"
HOMEPAGE = "https://github.com/testing-cabal/traceback2"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=31;endline=31;md5=368ddc8588431c180ae7c33f4fb57519"

DEPENDS = "${PYTHON_PN}-pbr-native"

SRC_URI[md5sum] = "9e9723f4d70bfc6308fa992dd193c400"
SRC_URI[sha256sum] = "05acc67a09980c2ecfedd3423f7ae0104839eccb55fc645773e1caa0951c3030"

inherit pypi setuptools

CLEANBROKEN = "1"

RDEPENDS_${PN} = "${PYTHON_PN}-linecache2"
