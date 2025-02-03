require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "6b56d834cc94c8b21a8f4e775064896be3b4a4ca387f2612d4406a5927cd2fdc"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 4.x branch, 
# PREFERRED_VERSION_python3-django = "4.2.17" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
