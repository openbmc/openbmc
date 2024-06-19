SUMMARY = "PEP 621 metadata parsing"
DESCRIPTION = "Dataclass for PEP 621 metadata with support for core \
metadata generation \
\
This project does not implement the parsing of pyproject.toml containing \
PEP 621 metadata.\
\
Instead, given a Python data structure representing PEP 621 metadata \
(already parsed), it will validate this input and generate a \
PEP 643-compliant metadata file (e.g. PKG-INFO)."
HOMEPAGE = "https://github.com/FFY00/python-pyproject-metadata"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=310439af287b0fb4780b2ad6907c256c"

PYPI_PACKAGE = "pyproject_metadata"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "376d5a00764ac29440a54579f88e66b7d9cb7e629d35c35a1c7248bfebc9b455"

RDEPENDS:${PN} += " \
    python3-logging \
    python3-packaging \
    python3-profile \
"

BBCLASSEXTEND = "native nativesdk"
