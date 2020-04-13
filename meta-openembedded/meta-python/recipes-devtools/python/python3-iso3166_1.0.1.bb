SUMMARY = "Self-contained ISO 3166-1 country definitions"
HOMEPAGE = "https://pypi.org/project/iso3166/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=5e2f4edc7e7408a82e4a1d05f229b695"

SRC_URI[md5sum] = "53c313c7ae8721e40ddd5e7a01bbcb7e"
SRC_URI[sha256sum] = "b1e58dbcf50fbb2c9c418ec7a6057f0cdb30b8f822ac852f72e71ba769dae8c5"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-numbers"

BBCLASSEXTEND = "native nativesdk"
