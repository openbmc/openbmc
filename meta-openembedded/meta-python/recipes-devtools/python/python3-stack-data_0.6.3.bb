SUMMARY = "Extract data from python stack frames and tracebacks for informative displays"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

PYPI_PACKAGE = "stack_data"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "836a778de4fec4dcd1dcd89ed8abff8a221f58308462e1c4aa2a3cf30148f0b9"

RDEPENDS:${PN} = " \
    python3-asttokens \
    python3-executing \
    python3-html \
    python3-logging \
    python3-pure-eval \
"
