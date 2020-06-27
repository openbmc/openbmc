SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
AUTHOR = "Anthon van der Neut"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=60afc0a1bb0501c0c555cabe78bba022"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

SRC_URI[md5sum] = "02774e7ed3273b3d8eee6c08326b91c4"
SRC_URI[sha256sum] = "099c644a778bf72ffa00524f78dd0b6476bca94a1da344130f4bf3381ce5b954"

do_install_prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}
