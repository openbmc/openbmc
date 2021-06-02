SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
AUTHOR = "Anthon van der Neut"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa0a51dfb461e2f803969e0f3fa71dfe"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "5c3fa739bbedd2f23769656784e671c6335d17a5bf163c3c3901d8663c0af287"

do_install_prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
