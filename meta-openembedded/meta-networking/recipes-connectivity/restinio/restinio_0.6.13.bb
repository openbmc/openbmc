SUMMARY = "Header-only C++14 library that gives you an embedded HTTP server"
DESCRIPTION = "Cross-platform, efficient, customizable, and robust \
               asynchronous HTTP/WebSocket server C++14 library with the \
               right balance between performance and ease of use"
HOMEPAGE = "https://stiffstream.com/en/products/restinio.html"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=f399b62ce0a152525d1589a5a40c0ff6"
DEPENDS = "asio fmt http-parser"

SRC_URI = "https://github.com/Stiffstream/restinio/releases/download/v.${PV}/${BP}.tar.bz2"
SRC_URI[md5sum] = "37a4310e98912030a74bdd4ed789f33c"
SRC_URI[sha256sum] = "b35d696e6fafd4563ca708fcecf9d0cf6705c846d417b5000f5252e0188848e7"

S = "${WORKDIR}/${BP}/dev"

inherit cmake

EXTRA_OECMAKE += "\
                  -DRESTINIO_TEST=OFF \
                  -DRESTINIO_SAMPLE=OFF \
                  -DRESTINIO_BENCH=OFF \
                  -DRESTINIO_FIND_DEPS=ON \
                  -DRESTINIO_ALLOW_SOBJECTIZER=OFF \
                  -DRESTINIO_USE_EXTERNAL_HTTP_PARSER=ON \
                  "

# Header-only library
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
