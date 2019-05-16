DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "flex-native bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz \
           file://0001-build-don-t-look-for-Iconv.patch \
           file://0001-Fix-6906.patch"
SRC_URI[md5sum] = "2c98c73eba392d334f5bbaf15e09bae3"
SRC_URI[sha256sum] = "bd9c0ec462b6a9b5b41ede97bede5458e0d7bb40d4cfa27f6f622eb33c59245d"

inherit cmake python3native

BBCLASSEXTEND = "native"
