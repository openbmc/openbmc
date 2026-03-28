require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "90be765ee756af8a6cbd6693e56452404b5ad15294f4d5e40c0a55a0f4870fe1"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
