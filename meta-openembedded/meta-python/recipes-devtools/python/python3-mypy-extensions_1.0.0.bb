SUMMARY = "Experimental type system extensions for programs checked with the mypy typechecker"
HOMEPAGE = "https://github.com/python/mypy_extensions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fe3219e2470a78c0d1837019b8b426e"

PYPI_PACKAGE = "mypy_extensions"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3

SRC_URI[sha256sum] = "75dbf8955dc00442a438fc4d0666508a9a97b6bd41aa2f0ffe9d2f2725af0782"

BBCLASSEXTEND = "native"
