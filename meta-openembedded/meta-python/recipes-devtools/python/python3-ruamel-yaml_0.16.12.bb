SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
AUTHOR = "Anthon van der Neut"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=60afc0a1bb0501c0c555cabe78bba022"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "c7e4b216d9554d80be42011b448b7c61"
SRC_URI[sha256sum] = "076cc0bc34f1966d920a49f18b52b6ad559fbe656a0748e3535cf7b3f29ebf9e"

do_install_prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
