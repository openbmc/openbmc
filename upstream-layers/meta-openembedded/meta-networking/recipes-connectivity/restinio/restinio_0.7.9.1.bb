SUMMARY = "Header-only C++14 library that gives you an embedded HTTP server"
DESCRIPTION = "Cross-platform, efficient, customizable, and robust \
               asynchronous HTTP/WebSocket server C++14 library with the \
               right balance between performance and ease of use"
HOMEPAGE = "https://stiffstream.com/en/products/restinio.html"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=d9ef67870895758240c3889695ad2558"
DEPENDS = "asio fmt llhttp expected-lite"

SRC_URI = "https://github.com/Stiffstream/restinio/releases/download/v${PV}/${BP}.tar.bz2"
SRC_URI[sha256sum] = "4a0429e2ea4ece200228226f0c628ecf9ac111cb6a6b12ad857de84c0576bf80"

UPSTREAM_CHECK_URI = "https://github.com/Stiffstream/restinio/tags"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

S = "${UNPACKDIR}/${BP}/dev"

inherit cmake

# restinio 0.7.x no longer bundles its third-party dependencies in the
# release tarball (unlike 0.6.x). fmt and expected-lite are resolved via
# find_package() against externally-provided packages. llhttp's upstream
# CMakeLists.txt only exports an "llhttp::llhttp_shared" imported target
# (llhttp::llhttp is merely a build-tree ALIAS, never installed/exported),
# which restinio's dev/restinio/CMakeLists.txt does not recognize as a
# valid find_package() target name, so llhttp is consumed in "system"
# mode instead (plain -lllhttp against the sysroot). The optional
# sobjectizer integration is disabled since we don't need it.
EXTRA_OECMAKE += "\
                  -DRESTINIO_TEST=OFF \
                  -DRESTINIO_SAMPLE=OFF \
                  -DRESTINIO_BENCHMARK=OFF \
                  -DRESTINIO_WITH_SOBJECTIZER=OFF \
                  -DRESTINIO_DEP_STANDALONE_ASIO=find \
                  -DRESTINIO_DEP_LLHTTP=system \
                  -DRESTINIO_DEP_FMT=find \
                  -DRESTINIO_DEP_EXPECTED_LITE=find \
                  "

# Header-only library
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
