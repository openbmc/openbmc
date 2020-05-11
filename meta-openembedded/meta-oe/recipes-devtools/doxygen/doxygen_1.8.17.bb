DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "flex-native bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz \
           file://0001-build-don-t-look-for-Iconv.patch \
"
SRC_URI_append_class-native = " file://doxygen-native-only-check-python3.patch"
SRC_URI[md5sum] = "7997a15c73a8bd6d003eaba5c2ee2b47"
SRC_URI[sha256sum] = "2cba988af2d495541cbbe5541b3bee0ee11144dcb23a81eada19f5501fd8b599"

inherit cmake python3native

BBCLASSEXTEND = "native nativesdk"
