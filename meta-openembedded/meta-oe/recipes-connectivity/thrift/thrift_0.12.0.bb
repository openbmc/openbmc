SUMMARY = "Apache Thrift"
DESCRIPTION =  "A software framework, for scalable cross-language services development"
HOMEPAGE = "https://thrift.apache.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=394465e125cffc0f133695ed43f14047 \
                    file://NOTICE;md5=42748ae4646b45fbfa5182807321fb6c"

DEPENDS = "thrift-native boost flex-native bison-native openssl"

SRC_URI = "https://www-eu.apache.org/dist/thrift//${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[md5sum] = "3deebbb4d1ca77dd9c9e009a1ea02183"
SRC_URI[sha256sum] = "c336099532b765a6815173f62df0ed897528a9d551837d627c1f87fadad90428"

BBCLASSEXTEND = "native nativesdk"

inherit pkgconfig cmake pythonnative

export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS

EXTRA_OECMAKE = " \
    -DENABLE_PRECOMPILED_HEADERS=OFF \
    -DBUILD_LIBRARIES=ON \
    -DBUILD_COMPILER=ON \
    -DBUILD_TESTING=OFF \
    -DBUILD_EXAMPLES=OFF \
    -DBUILD_TUTORIALS=OFF \
    -DWITH_CPP=ON \
    -DWITH_JAVA=OFF \
    -DWITH_PYTHON=OFF \
    -DWITH_STATIC_LIB=ON \
    -DWITH_SHARED_LIB=ON \
    -DWITH_OPENSSL=ON \
    -DWITH_QT4=OFF \
    -DWITH_QT5=OFF \
    -DWITH_BOOST_FUNCTIONAL=OFF \
"

PACKAGECONFIG ??= "libevent glib boost-smart-ptr"
PACKAGECONFIG[libevent] = "-DWITH_LIBEVENT=ON,-DWITH_LIBEVENT=OFF,libevent"
PACKAGECONFIG[glib] = "-DWITH_C_GLIB=ON,-DWITH_C_GLIB=OFF,glib-2.0"
PACKAGECONFIG[boost-smart-ptr] = "-DWITH_BOOST_SMART_PTR=ON,-DWITH_BOOST_SMART_PTR=OFF,boost"

do_install_append () {
    ln -sf thrift ${D}/${bindir}/thrift-compiler
}

LEAD_SONAME = "libthrift.so.${PV}"

# thrift packages
PACKAGE_BEFORE_PN = "${PN}-compiler lib${BPN} lib${BPN}z lib${BPN}nb lib${BPN}-c-glib"
FILES_lib${BPN} = "${libdir}/libthrift.so.*"
FILES_lib${BPN}z = "${libdir}/libthriftz.so.*"
FILES_lib${BPN}nb = "${libdir}/libthriftnb.so.*"
FILES_lib${BPN}-c-glib = "${libdir}/libthrift_c_glib.so.*"
FILES_${PN}-compiler = "${bindir}/*"

# The thrift packages just pulls in some default dependencies but is otherwise empty
RRECOMMENDS_${PN} = "${PN}-compiler lib${BPN}"
ALLOW_EMPTY_${PN} = "1"
RRECOMMENDS_${PN}_class-native = ""
RRECOMMENDS_${PN}_class-nativesdk = ""
