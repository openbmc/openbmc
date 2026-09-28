require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "2998503fc083124fb58037084bfa00de323c7c743f05f1b4284e77bff0ab8890"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
