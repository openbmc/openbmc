SUMMARY = "Extract data from python stack frames and tracebacks for informative displays"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a3d6c15f7859ae235a78f2758e5a48cf"

DEPENDS = "python3-setuptools-scm-native"

PYPI_PACKAGE = "stack_data"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "32d2dd0376772d01b6cb9fc996f3c8b57a357089dec328ed4b6553d037eaf815"

RDEPENDS:${PN} = " \
	python3-executing \
	python3-asttokens \
	python3-pure-eval \
"
