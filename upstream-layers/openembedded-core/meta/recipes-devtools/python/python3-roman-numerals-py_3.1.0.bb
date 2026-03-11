SUMMARY = "Manipulate roman numerals"
HOMEPAGE = "https://github.com/AA-Turner/roman-numerals/"
LICENSE = "0BSD & CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=bfcc8b16e42929aafeb9d414360bc2fd"

SRC_URI[sha256sum] = "be4bf804f083a4ce001b5eb7e3c0862479d10f94c936f6c4e5f250aa5ff5bd2d"
PYPI_PACKAGE = "roman_numerals_py"
UPSTREAM_CHECK_PYPI_PACKAGE = "roman_numerals_py"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
