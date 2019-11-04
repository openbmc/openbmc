DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "flex-native bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz \
           file://0001-build-don-t-look-for-Iconv.patch \
"
SRC_URI[md5sum] = "3ec5f8bfda38a05845161fbbd5d8b439"
SRC_URI[sha256sum] = "ff981fb6f5db4af9deb1dd0c0d9325e0f9ba807d17bd5750636595cf16da3c82"

inherit cmake python3native

BBCLASSEXTEND = "native"
