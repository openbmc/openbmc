require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "66c9d8db8cc6fe938a28b7887c1596e42d522e27618562517cc8929eb7e7f296"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
