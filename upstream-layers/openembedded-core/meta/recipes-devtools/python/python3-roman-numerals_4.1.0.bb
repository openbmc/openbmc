SUMMARY = "Manipulate roman numerals"
HOMEPAGE = "https://github.com/AA-Turner/roman-numerals/"
LICENSE = "0BSD & CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=bfcc8b16e42929aafeb9d414360bc2fd"

SRC_URI[sha256sum] = "1af8b147eb1405d5839e78aeb93131690495fe9da5c91856cb33ad55a7f1e5b2"
PYPI_PACKAGE = "roman_numerals"
UPSTREAM_CHECK_PYPI_PACKAGE = "roman_numerals"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
