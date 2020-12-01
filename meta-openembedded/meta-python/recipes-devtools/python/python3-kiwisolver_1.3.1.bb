SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://setup.py;endline=7;md5=e54bd74bd9d0a84ae3f8f6d21ada0ab4"

SRC_URI[md5sum] = "81012578317ddcfa3daed806142f8fed"
SRC_URI[sha256sum] = "950a199911a8d94683a6b10321f9345d5a3a8433ec58b217ace979e18f16e248"

inherit pypi setuptools3

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS_${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
