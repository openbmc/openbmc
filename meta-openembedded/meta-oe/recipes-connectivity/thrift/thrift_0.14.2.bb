SUMMARY = "Apache Thrift"
DESCRIPTION =  "A software framework, for scalable cross-language services development"
HOMEPAGE = "https://thrift.apache.org/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c40a383cb3f747e0c7abbf1482f194f0 \
                    file://NOTICE;md5=2659b43daca219f99a2f2626ea128f73"

DEPENDS = "thrift-native boost flex-native bison-native openssl zlib"

SRC_URI = "https://www-eu.apache.org/dist/thrift//${PV}/${BPN}-${PV}.tar.gz \
           file://0001-DefineInstallationPaths.cmake-Define-libdir-in-terms.patch \
          "
SRC_URI[md5sum] = "284a48df355aa3910687ee9b894d3ae8"
SRC_URI[sha256sum] = "4191bfc0b7490e20cc69f9f4dc6e991fbb612d4551aa9eef1dbf7f4c47ce554d"

BBCLASSEXTEND = "native nativesdk"

inherit pkgconfig cmake python3native

export STAGING_INCDIR
export STAGING_LIBDIR
export BUILD_SYS
export HOST_SYS

EXTRA_OECMAKE = " \
    -DBUILD_LIBRARIES=ON \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_COMPILER=ON \
    -DBUILD_TESTING=OFF \
    -DBUILD_TUTORIALS=OFF \
    -DWITH_AS3=OFF \
    -DWITH_CPP=ON \
    -DWITH_JAVA=OFF \
    -DWITH_OPENSSL=ON \
    -DWITH_QT5=OFF \
    -DWITH_ZLIB=ON \
"

PACKAGECONFIG ??= "glib libevent"
PACKAGECONFIG[glib] = "-DWITH_C_GLIB=ON,-DWITH_C_GLIB=OFF,glib-2.0"
PACKAGECONFIG[libevent] = "-DWITH_LIBEVENT=ON,-DWITH_LIBEVENT=OFF,libevent"
PACKAGECONFIG[javascript] = "-DWITH_JAVASCRIPT=ON,-DWITH_JAVASCRIPT=OFF,nodejs"
PACKAGECONFIG[nodejs] = "-DWITH_NODEJS=ON,-DWITH_NODEJS=OFF,nodejs"
PACKAGECONFIG[python] = "-DWITH_PYTHON=ON,-DWITH_PYTHON=OFF,python"

do_install:append () {
    ln -sf thrift ${D}/${bindir}/thrift-compiler
}

LEAD_SONAME = "libthrift.so.${PV}"

# thrift packages
PACKAGE_BEFORE_PN = "${PN}-compiler lib${BPN} lib${BPN}z lib${BPN}nb lib${BPN}-c-glib"
FILES:lib${BPN} = "${libdir}/libthrift.so.*"
FILES:lib${BPN}z = "${libdir}/libthriftz.so.*"
FILES:lib${BPN}nb = "${libdir}/libthriftnb.so.*"
FILES:lib${BPN}-c-glib = "${libdir}/libthrift_c_glib.so.*"
FILES:${PN}-compiler = "${bindir}/*"

# The thrift packages just pulls in some default dependencies but is otherwise empty
RRECOMMENDS:${PN} = "${PN}-compiler lib${BPN}"
ALLOW_EMPTY:${PN} = "1"
RRECOMMENDS:${PN}:class-native = ""
RRECOMMENDS:${PN}:class-nativesdk = ""
