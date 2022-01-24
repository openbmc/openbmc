require python-django.inc

# Pin to 2.2.x LTS releases ONLY for this recipe
UPSTREAM_CHECK_REGEX = "/${PYPI_PACKAGE}/(?P<pver>(2\.2\.\d*)+)/"

inherit setuptools3

SRC_URI[sha256sum] = "dfa537267d52c6243a62b32855a744ca83c37c70600aacffbfd98bc5d6d8518f"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
