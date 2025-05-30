SUMMARY = "pydot is is an interface to Graphviz."
HOMEPAGE = "https://github.com/pydot/pydot"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

SRC_URI[sha256sum] = "12f16493337cade2f7631b87c8ccd299ba2e251f3ee5d0732a058df2887afe97"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "graphviz python3-pyparsing"
