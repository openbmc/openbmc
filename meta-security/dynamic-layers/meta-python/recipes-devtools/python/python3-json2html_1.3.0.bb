DESCRIPTION="Python wrapper to convert JSON into a human readable HTML Table representation."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8065590663ea0c10aa131841ea806767"

SRC_URI[sha256sum] = "8951a53662ae9cfd812685facdba693fc950ffc1c1fd1a8a2d3cf4c34600689c"

PYPI_PACKAGE = "json2html"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native \
"
