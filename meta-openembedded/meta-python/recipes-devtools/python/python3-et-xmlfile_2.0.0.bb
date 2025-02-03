SUMMARY = "et_xmlfile is a low memory library for creating large XML files"
DESCRIPTION = "It is based upon the xmlfile module from lxml with the aim of allowing code \
to be developed that will work with both libraries. It was developed initially for \
the openpyxl project but is now a standalone module."

HOMEPAGE = "https://foss.heptapod.net/openpyxl/et_xmlfile"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

SRC_URI[sha256sum] = "dab3f4764309081ce75662649be815c4c9081e88f0837825f90fd28317d4da54"

RDEPENDS:${PN} += " \
    python3-compression \
    python3-io \
    python3-pprint \
    python3-shell \
    python3-xml \
"

inherit setuptools3 pypi

PYPI_PACKAGE = "et_xmlfile"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"
