DESCRIPTION = "Fully featured framework for fast, easy and documented API development with Flask"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c96dd911c6d9e32868b6bc667a38a3e2"

SRC_URI[sha256sum] = "4f3d3fa7b6191fcc715b18c201a12cd875176f92ba4acc61626ccfd571ee1728"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-aniso8601 \
    python3-jsonschema \
    python3-pytz \
"
