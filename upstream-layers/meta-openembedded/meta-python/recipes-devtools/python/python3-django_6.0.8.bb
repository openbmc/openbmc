require python3-django.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "cb0bd962d27fc866f3c514b20aae6a7df56ec80b488f9899da46d675cd051526"

# Set DEFAULT_PREFERENCE so that the LTS version of django is built by
# default. To build the 6.x branch,
# PREFERRED_VERSION_python3-django = "6.0.%" can be added to local.conf
DEFAULT_PREFERENCE = "-1"
