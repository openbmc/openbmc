SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
AUTHOR = "Anthon van der Neut"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=caf8bd842440b537c044e131785a4666"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

SRC_URI[md5sum] = "7d5a5b0a7621a1247b081cc8e4978354"
SRC_URI[sha256sum] = "412a6f5cfdc0525dee6a27c08f5415c7fd832a7afcb7a0ed7319628aed23d408"

do_install_prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}
