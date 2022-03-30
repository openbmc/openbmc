SUMMARY = "Python parsing module"
DESCRIPTION = "The pyparsing module is an alternative approach to creating \
and executing simple grammars, vs. the traditional lex/yacc approach, or \
the use of regular expressions. The pyparsing module provides a library of \
classes that client code uses to construct the grammar directly in Python \
code."
HOMEPAGE = "https://github.com/pyparsing/pyparsing/"
BUGTRACKER = "https://github.com/pyparsing/pyparsing/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=657a566233888513e1f07ba13e2f47f1"

SRC_URI[sha256sum] = "18ee9022775d270c55187733956460083db60b37d0d0fb357445f3094eed3eea"

UPSTREAM_CHECK_REGEX = "pyparsing-(?P<pver>.*)\.tar"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-debugger \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-stringold \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
