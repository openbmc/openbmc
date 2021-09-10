require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "3da05fea54fdec2315b54a563d5b59f3b4e2b1e69c3a5841dda35019c01855cd"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-sqlparse \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 3.x branch, 
# PREFERRED_VERSION_python3-django = "3.2.2" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
