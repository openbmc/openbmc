require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "32ce792ee9b6a0cbbec340123e229ac9f765dff8c2a4ae9247a14b2ba3a365a7"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.1.7" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
