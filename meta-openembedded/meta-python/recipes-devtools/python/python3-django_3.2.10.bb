require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "074e8818b4b40acdc2369e67dcd6555d558329785408dcd25340ee98f1f1d5c4"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
