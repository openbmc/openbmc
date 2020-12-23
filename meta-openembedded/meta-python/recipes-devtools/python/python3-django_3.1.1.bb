require python-django.inc
inherit setuptools3

SRC_URI[md5sum] = "d5e894fb3c46064e84e9dc68a08a46d0"
SRC_URI[sha256sum] = "59c8125ca873ed3bdae9c12b146fbbd6ed8d0f743e4cf5f5817af50c51f1fc2f"

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.1.1" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
