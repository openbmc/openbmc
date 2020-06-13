SUMMARY = "Apache Thrift"
DESCRIPTION =  "A software framework, for scalable cross-language services development"
HOMEPAGE = "https://thrift.apache.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=394465e125cffc0f133695ed43f14047 \
                    file://NOTICE;md5=2659b43daca219f99a2f2626ea128f73"

DEPENDS = "thrift-native boost flex-native bison-native openssl"

SRC_URI = "https://www-eu.apache.org/dist/thrift//${PV}/${BPN}-${PV}.tar.gz \
           file://0001-DefineInstallationPaths.cmake-Define-libdir-in-terms.patch \
          "
SRC_URI[md5sum] = "38a27d391a2b03214b444cb13d5664f1"
SRC_URI[sha256sum] = "7ad348b88033af46ce49148097afe354d513c1fca7c607b59c33ebb6064b5179"

BBCLASSEXTEND = "native nativesdk"

inherit pkgconfig cmake python3native

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
    -DWITH_PYTHON=OFF \
    -DWITH_STATIC_LIB=ON \
    -DWITH_SHARED_LIB=ON \
    -DWITH_OPENSSL=ON \
    -DWITH_QT5=OFF \
"

PACKAGECONFIG ??= "libevent glib"
PACKAGECONFIG[libevent] = "-DWITH_LIBEVENT=ON,-DWITH_LIBEVENT=OFF,libevent"
PACKAGECONFIG[glib] = "-DWITH_C_GLIB=ON,-DWITH_C_GLIB=OFF,glib-2.0"

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
