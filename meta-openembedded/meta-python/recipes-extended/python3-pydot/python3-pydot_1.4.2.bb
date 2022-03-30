SUMMARY = "pydot is is an interface to Graphviz."
HOMEPAGE = "https://github.com/pydot/pydot"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f6fa041dfcc7ff7747cfceaa34a3180"

SRC_URI[sha256sum] = "248081a39bcb56784deb018977e428605c1c758f10897a339fce1dd728ff007d"

inherit pypi setuptools3

RDEPENDS:${PN} = "graphviz python3-pyparsing"
