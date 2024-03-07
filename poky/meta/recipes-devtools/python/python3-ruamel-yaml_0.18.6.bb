SUMMARY = "YAML parser/emitter that supports roundtrip preservation of comments, seq/map flow style, and map key order."
HOMEPAGE = "https://pypi.org/project/ruamel.yaml/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=30cbbccd94bf3a2b0285ec35671a1938"

PYPI_PACKAGE = "ruamel.yaml"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8b27e6a217e786c6fbe5634d8f3f11bc63e0f80f6a5890f28863d9c45aac311b"

RDEPENDS:${PN} += "\
    python3-shell \
    python3-datetime \
    python3-netclient \
"

do_install:prepend() {
    export RUAMEL_NO_PIP_INSTALL_CHECK=1
}

BBCLASSEXTEND = "native nativesdk"
