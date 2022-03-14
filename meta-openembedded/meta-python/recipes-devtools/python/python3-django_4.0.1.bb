require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "2485eea3cc4c3bae13080dee866ebf90ba9f98d1afe8fda89bfb0eb2e218ef86"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 4.x branch, 
# PREFERRED_VERSION_python3-django = "4.0.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
