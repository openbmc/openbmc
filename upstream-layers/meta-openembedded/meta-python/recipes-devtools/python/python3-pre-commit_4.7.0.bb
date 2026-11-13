SUMMARY = "A framework for managing and maintaining multi-language pre-commit hooks."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b53a93744e3ff841e5fc9a934da8e1c8"

inherit pypi setuptools3
SRC_URI[sha256sum] = "1f3deee12b914c8fa184bd2b5953391c2e4f36763e403d4a23dc9983fa7c6e86"

RDEPENDS:${PN} = " \
	python3-cfgv \
	python3-identify \
	python3-nodeenv \
	python3-pyyaml \
	python3-virtualenv \
"
