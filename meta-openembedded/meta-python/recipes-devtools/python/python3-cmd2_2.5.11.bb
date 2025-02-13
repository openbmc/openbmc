SUMMARY = "Extra features for standard library's cmd module"
HOMEPAGE = "https://github.com/python-cmd2/cmd2"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=01d2b71040983a2abc614fc4d0284771"

DEPENDS += "python3-setuptools-scm-native"

SRC_URI[sha256sum] = "30a0d385021fbe4a4116672845e5695bbe56eb682f9096066776394f954a7429"

inherit pypi python_setuptools_build_meta python3native

RDEPENDS:${PN} += "\
    python3-attrs \
    python3-colorama \
    python3-pyperclip \
    python3-wcwidth \
    python3-compression \
    python3-pydoc \
    python3-json \
    python3-numbers \
"

BBCLASSEXTEND = "native nativesdk"
