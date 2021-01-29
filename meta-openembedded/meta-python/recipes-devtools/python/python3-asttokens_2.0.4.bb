SUMMARY = "The asttokens module annotates Python abstract syntax trees (ASTs)"
HOMEPAGE = "https://github.com/gristlabs/asttokens"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

PYPI_PACKAGE = "asttokens"

inherit pypi setuptools3

SRC_URI[sha256sum] = "a42e57e28f2ac1c85ed9b1f84109401427e5c63c04f61d15b8842b027eec5128"

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-wheel-native \
"

BBCLASSEXTEND = "native"
