require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "21f0f9643722675976004eb683c55d33c05486f94506672df3d6a141546f389d"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
