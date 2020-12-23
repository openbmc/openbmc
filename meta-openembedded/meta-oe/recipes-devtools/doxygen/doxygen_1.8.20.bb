DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "flex-native bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz \
           file://0001-build-don-t-look-for-Iconv.patch \
"
SRC_URI_append_class-native = " file://doxygen-native-only-check-python3.patch"
SRC_URI[md5sum] = "8729936a843232a66fe970ef65f3c3e4"
SRC_URI[sha256sum] = "e0db6979286fd7ccd3a99af9f97397f2bae50532e4ecb312aa18862f8401ddec"

inherit cmake python3native

BBCLASSEXTEND = "native nativesdk"
