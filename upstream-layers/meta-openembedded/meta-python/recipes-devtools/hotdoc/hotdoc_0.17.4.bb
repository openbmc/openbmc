SUMMARY = "Hotdoc is a documentation framework"
DESCRIPTION = "Hotdoc is a documentation micro-framework. It provides an interface for extensions to plug upon, along with some base objects (formatters, ...)"
HOMEPAGE = "https://github.com/hotdoc/hotdoc"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=90263a49bc1d9a204656fec4d5616c66"

SRC_URI[sha256sum] = "c4d5dff647f03aa87a1d2d06035d2819edd099b91635e3b2ee390829357ae9fc"

SRC_URI = "file://0001-c_comment_scanner-fix-function-prototypes.patch \
           file://0002-avoid-third-party-backports-dependency-on-sufficient.patch \
           file://0003-CMake-4-compatibility.patch;patchdir=cmark \
          "

SRC_URI:append:class-target = "file://0004-Use-flex-with-noline-option-to-prevent.patch"

DEPENDS += "libxml2 glib-2.0 json-glib"

inherit pypi python_setuptools_build_meta pkgconfig

#Fix LIBDIR path to fix buildpaths QA check of hotdoc/parsers/cmark.cpython-314-x86_64-linux-gnu.so
do_configure:prepend:class-target() {
    sed -i -e "s#'\"%s\"' % CMARK_BUILD_DIR#'\"${PYTHON_SITEPACKAGES_DIR}/${PN}\"'#" ${S}/setup.py
}

RDEPENDS:${PN} += "clang python3-appdirs python3-cchardet python3-dbus-deviation python3-lxml python3-networkx python3-pkgconfig python3-pyyaml python3-schema python3-toposort python3-wheezy-template"

BBCLASSEXTEND = "native"
