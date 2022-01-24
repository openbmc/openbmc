require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "69c94abe5d6b1b088bf475e09b7b74403f943e34da107e798465d2045da27e75"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
