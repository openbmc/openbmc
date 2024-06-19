SUMMARY = "SWIG - Simplified Wrapper and Interface Generator"
DESCRIPTION = "SWIG is a compiler that makes it easy to integrate C and C++ \
code with other languages including Perl, Tcl, Ruby, Python, Java, Guile, \
Mzscheme, Chicken, OCaml, Pike, and C#."
HOMEPAGE = "http://swig.sourceforge.net/"
LICENSE = "BSD-3-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e7807a6282784a7dde4c846626b08fc6 \
                    file://LICENSE-GPL;md5=d32239bcb673463ab874e80d47fae504 \
                    file://LICENSE-UNIVERSITIES;md5=8ce9dcc8f7c994de4a408b205c72ba08"

SECTION = "devel"

DEPENDS = "libpcre2 bison-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
            file://determinism.patch \
           "
SRC_URI[sha256sum] = "fa045354e2d048b2cddc69579e4256245d4676894858fcf0bab2290ecf59b7d8"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/swig/files/swig/"
UPSTREAM_CHECK_REGEX = "swig-(?P<pver>\d+(\.\d+)+)"

inherit cmake pkgconfig

BBCLASSEXTEND = "native nativesdk"

do_install:append:class-nativesdk() {
    cd ${D}${bindir}
    ln -s swig swig2.0
}

def swiglib_relpath(d):
    swiglib = d.getVar('datadir') + "/" + d.getVar('BPN') + "/" + d.getVar('PV')
    return os.path.relpath(swiglib, d.getVar('bindir'))

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/swig SWIG_LIB='`dirname $''realpath`'/${@swiglib_relpath(d)}
}

PACKAGE_PREPROCESS_FUNCS += "src_package_preprocess"
src_package_preprocess () {
        # Trim build paths from comments and defines in generated sources to ensure reproducibility
        sed -i -e "s,${WORKDIR},,g" \
            -e "s,YY_YY_.*_CPARSE_PARSER_H_INCLUDED,YY_YY_CPARSE_PARSER_H_INCLUDED,g" \
            ${B}/Source/CParse/parser.*
}
