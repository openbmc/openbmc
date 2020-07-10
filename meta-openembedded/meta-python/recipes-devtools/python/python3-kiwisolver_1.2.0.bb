SUMMARY = "A fast implementation of the Cassowary constraint solver"
HOMEPAGE = "https://github.com/nucleic/kiwi"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://setup.py;endline=7;md5=e54bd74bd9d0a84ae3f8f6d21ada0ab4"

SRC_URI[md5sum] = "9f53fabb361c04d76c5afc688652c814"
SRC_URI[sha256sum] = "247800260cd38160c362d211dcaf4ed0f7816afb5efe56544748b21d6ad6d17f"

inherit pypi setuptools3

DEPENDS += "\
    python3-cppy-native \
"

RDEPENDS_${PN} += "\
    python3-core \
    python3-setuptools \
"

BBCLASSEXTEND = "native nativesdk"
