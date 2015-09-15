SUMMARY = "Templating library for Python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=deb3ff8e4c17aaf7b80889b6b2bf4c83"

SRC_URI = "https://pypi.python.org/packages/source/M/Mako/Mako-${PV}.tar.gz"

SRC_URI[md5sum] = "9f0aafd177b039ef67b90ea350497a54"
SRC_URI[sha256sum] = "45f0869febea59dab7efd256fb451c377cbb7947bef386ff0bb44627c31a8d1c"

S = "${WORKDIR}/Mako-${PV}"

inherit setuptools

RDEPENDS_${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
