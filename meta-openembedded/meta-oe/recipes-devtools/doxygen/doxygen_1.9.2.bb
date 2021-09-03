DESCRIPTION = "Doxygen is the de facto standard tool for generating documentation from annotated C++ sources."
HOMEPAGE = "http://www.doxygen.org/"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "flex-native bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BP}.src.tar.gz \
           file://0001-build-don-t-look-for-Iconv.patch \
"
SRC_URI:append:class-native = " file://doxygen-native-only-check-python3.patch"
SRC_URI[sha256sum] = "060f254bcef48673cc7ccf542736b7455b67c110b30fdaa33512a5b09bbecee5"

inherit cmake python3native

BBCLASSEXTEND = "native nativesdk"
