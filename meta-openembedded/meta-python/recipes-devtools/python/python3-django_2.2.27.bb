require python-django.inc

# Pin to 2.2.x LTS releases ONLY for this recipe
UPSTREAM_CHECK_REGEX = "/${PYPI_PACKAGE}/(?P<pver>(2\.2\.\d*)+)/"

inherit setuptools3

SRC_URI[sha256sum] = "1ee37046b0bf2b61e83b3a01d067323516ec3b6f2b17cd49b1326dd4ba9dc913"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"
