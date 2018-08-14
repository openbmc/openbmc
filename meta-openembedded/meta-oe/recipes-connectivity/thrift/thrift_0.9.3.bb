SUMMARY = "Apache Thrift"
DESCRIPTION =  "A software framework, for scalable cross-language services development"
HOMEPAGE = "https://thrift.apache.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e4ed21f679b2aafef26eac82ab0c2cbf \
                    file://NOTICE;md5=115f49498b66b494b0472658f2bfe80b"

DEPENDS = "thrift-native boost flex-native bison-native openssl"

SRC_URI = "http://mirror.switch.ch/mirror/apache/dist/thrift/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Forcibly-disable-check-for-Qt5.patch \
           file://0001-THRIFT-3828-In-cmake-avoid-use-of-both-quoted-paths-.patch \
           file://0002-THRIFT-3831-in-test-cpp-explicitly-use-signed-char.patch \
"

SRC_URI[md5sum] = "88d667a8ae870d5adeca8cb7d6795442"
SRC_URI[sha256sum] = "b0740a070ac09adde04d43e852ce4c320564a292f26521c46b78e0641564969e"

BBCLASSEXTEND = "native nativesdk"

inherit pkgconfig cmake pythonnative

export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS

EXTRA_OECMAKE = " \
    -DBUILD_LIBRARIES=ON \
    -DBUILD_COMPILER=ON \
    -DBUILD_TESTING=OFF \
    -DBUILD_EXAMPLES=OFF \
    -DBUILD_TUTORIALS=OFF \
    -DWITH_CPP=ON \
    -DWITH_JAVA=OFF \
    -DWITH_STATIC_LIB=ON \
    -DWITH_SHARED_LIB=ON \
    -DWITH_OPENSSL=ON \
    -DWITH_QT4=OFF \
    -DWITH_QT5=OFF \
"

PACKAGECONFIG ??= "libevent glib python"
PACKAGECONFIG[libevent] = "-DWITH_LIBEVENT=ON,-DWITH_LIBEVENT=OFF,libevent,"
PACKAGECONFIG[python] = "-DWITH_PYTHON=ON,-DWITH_PYTHON=OFF,python,"
PACKAGECONFIG[glib] = "-DWITH_C_GLIB=ON,-DWITH_C_GLIB=OFF,glib-2.0 ,"

do_install_append () {
    ln -sf thrift ${D}/${bindir}/thrift-compiler
}

LEAD_SONAME = "libthrift.so.${PV}"

# thrift packages
PACKAGE_BEFORE_PN = "${PN}-compiler lib${BPN}"
FILES_lib${BPN} = "${libdir}/*.so.*"
FILES_${PN}-compiler = "${bindir}/*"

# The thrift packages just pulls in some default dependencies but is otherwise empty
RRECOMMENDS_${PN} = "${PN}-compiler lib${BPN}"
ALLOW_EMPTY_${PN} = "1"
RRECOMMENDS_${PN}_class-native = ""
RRECOMMENDS_${PN}_class-nativesdk = ""
