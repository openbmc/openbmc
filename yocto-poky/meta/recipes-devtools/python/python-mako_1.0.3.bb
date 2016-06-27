SUMMARY = "Templating library for Python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=deb3ff8e4c17aaf7b80889b6b2bf4c83"

SRC_URI = "https://pypi.python.org/packages/source/M/Mako/Mako-${PV}.tar.gz"

SRC_URI[md5sum] = "a78f20f6366a8a0659ce5532f8614e53"
SRC_URI[sha256sum] = "7644bc0ee35965d2e146dde31827b8982ed70a58281085fac42869a09764d38c"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/mako/"
UPSTREAM_CHECK_REGEX = "/Mako/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/Mako-${PV}"

inherit setuptools

RDEPENDS_${PN} = "python-threading \
                  python-netclient \
                  python-html \
"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
