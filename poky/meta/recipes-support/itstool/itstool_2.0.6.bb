SUMMARY = "ITS Tool allows you to translate your XML documents with PO files"
DESCRIPTION = "It extracts messages from XML files and outputs PO template \
files, then merges translations from MO files to create translated \
XML files. It determines what to translate and how to chunk it into \
messages using the W3C Internationalization Tag Set (ITS). "
HOMEPAGE = "http://itstool.org/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=59c57b95fd7d0e9e238ebbc7ad47c5a5"

inherit autotools python3native

DEPENDS = "libxml2-native"

SRC_URI = "http://files.itstool.org/${BPN}/${BPN}-${PV}.tar.bz2"
SRC_URI_append_class-native = " file://0001-Native-Don-t-use-build-time-hardcoded-python-binary-.patch"
SRC_URI_append_class-nativesdk = " file://0001-Native-Don-t-use-build-time-hardcoded-python-binary-.patch"
SRC_URI_append_class-target = " file://0002-Don-t-use-build-time-hardcoded-python-binary-path.patch"

SRC_URI[md5sum] = "4306eeba4f4aee6b393d14f9c3c57ca1"
SRC_URI[sha256sum] = "6233cc22726a9a5a83664bf67d1af79549a298c23185d926c3677afa917b92a9"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS_${PN} += "libxml2-python"
