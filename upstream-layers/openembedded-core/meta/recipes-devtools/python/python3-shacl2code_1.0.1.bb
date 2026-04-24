SUMMARY = "Convert SHACL model to code bindings"
HOMEPAGE = "https://pypi.org/project/shacl2code/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0582f358628f299f29c23bf5fb2f73c9"

PYPI_PACKAGE = "shacl2code"
SRC_URI[sha256sum] = "c856822b40c330452b8b31e94a658ad4595a5ef03cdb75ea432ea9c73d0cf7d9"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
    python3-jinja2 \
    python3-rdflib \
"

BBCLASSEXTEND = "native nativesdk"
