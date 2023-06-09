SUMMARY = "Safely evaluate AST nodes without side effects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

PYPI_PACKAGE = "pure_eval"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "2b45320af6dfaa1750f543d714b6d1c520a1688dec6fd24d339063ce0aaa9ac3"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-numbers \
"
