SUMMARY = "pydot is is an interface to Graphviz."
HOMEPAGE = "https://github.com/pydot/pydot"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

SRC_URI[sha256sum] = "3ce88b2558f3808b0376f22bfa6c263909e1c3981e2a7b629b65b451eee4a25d"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = "graphviz python3-pyparsing"
