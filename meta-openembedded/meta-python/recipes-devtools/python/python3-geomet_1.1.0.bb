SUMMARY = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
DESCRIPTION = "Convert GeoJSON to WKT/WKB (Well-Known Text/Binary), and vice versa."
HOMEPAGE = "https://github.com/geomet/geomet"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"
SRCNAME = "geomet"

SRC_URI[sha256sum] = "51e92231a0ef6aaa63ac20c443377ba78a303fd2ecd179dc3567de79f3c11605"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-click \
    python3-core \
    python3-io \
    python3-json \
    python3-logging \
    python3-six \
"
