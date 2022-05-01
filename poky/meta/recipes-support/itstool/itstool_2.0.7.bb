SUMMARY = "ITS Tool allows you to translate your XML documents with PO files"
DESCRIPTION = "It extracts messages from XML files and outputs PO template \
files, then merges translations from MO files to create translated \
XML files. It determines what to translate and how to chunk it into \
messages using the W3C Internationalization Tag Set (ITS). "
HOMEPAGE = "http://itstool.org/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=59c57b95fd7d0e9e238ebbc7ad47c5a5 \
    file://COPYING.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit autotools python3native

DEPENDS = "libxml2-native"

SRC_URI = "http://files.itstool.org/${BPN}/${BPN}-${PV}.tar.bz2 \
           "
UPSTREAM_CHECK_URI = "https://itstool.org/download.html"

SRC_URI:append:class-native = " file://0001-Native-Don-t-use-build-time-hardcoded-python-binary-.patch"
SRC_URI:append:class-nativesdk = " file://0001-Native-Don-t-use-build-time-hardcoded-python-binary-.patch"
SRC_URI:append:class-target = " file://0002-Don-t-use-build-time-hardcoded-python-binary-path.patch"

SRC_URI[sha256sum] = "6b9a7cd29a12bb95598f5750e8763cee78836a1a207f85b74d8b3275b27e87ca"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "libxml2-python"
