SUMMARY = "pydot is is an interface to Graphviz."
HOMEPAGE = "https://github.com/pydot/pydot"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

SRC_URI[sha256sum] = "c2148f681c4a33e08bf0e26a9e5f8e4099a82e0e2a068098f32ce86577364ad5"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "graphviz python3-pyparsing"
