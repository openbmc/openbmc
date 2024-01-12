SUMMARY = "pydot is is an interface to Graphviz."
HOMEPAGE = "https://github.com/pydot/pydot"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f6fa041dfcc7ff7747cfceaa34a3180"

SRC_URI[sha256sum] = "60246af215123fa062f21cd791be67dda23a6f280df09f68919e637a1e4f3235"

inherit pypi setuptools3

RDEPENDS:${PN} = "graphviz python3-pyparsing"
