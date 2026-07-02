SUMMARY = "Header-only C++14 library that gives you an embedded HTTP server"
DESCRIPTION = "Cross-platform, efficient, customizable, and robust \
               asynchronous HTTP/WebSocket server C++14 library with the \
               right balance between performance and ease of use"
HOMEPAGE = "https://stiffstream.com/en/products/restinio.html"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=bfe2747443b7875eaaaa94b55b611f49"
DEPENDS = "asio fmt http-parser"

SRC_URI = "https://github.com/Stiffstream/restinio/releases/download/v.${PV}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "7f21eebeb966e170d6ab54e9b198ac927d439090121739e037c0fa3bb367b240"

S = "${UNPACKDIR}/${BP}/dev"

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
