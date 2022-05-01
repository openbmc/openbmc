require python-django.inc

# Pin to 2.2.x LTS releases ONLY for this recipe
UPSTREAM_CHECK_REGEX = "/${PYPI_PACKAGE}/(?P<pver>(2\.2\.\d*)+)/"

inherit setuptools3

SRC_URI[sha256sum] = "0200b657afbf1bc08003845ddda053c7641b9b24951e52acd51f6abda33a7413"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
