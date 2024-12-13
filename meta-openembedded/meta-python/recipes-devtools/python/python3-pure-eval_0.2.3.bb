SUMMARY = "Safely evaluate AST nodes without side effects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

PYPI_PACKAGE = "pure_eval"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "5f4e983f40564c576c7c8635ae88db5956bb2229d7e9237d03b3c0b0190eaf42"

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-numbers \
"
