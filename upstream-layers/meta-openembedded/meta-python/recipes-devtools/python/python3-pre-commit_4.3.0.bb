SUMMARY = "A framework for managing and maintaining multi-language pre-commit hooks."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b53a93744e3ff841e5fc9a934da8e1c8"

PYPI_PACKAGE = "pre_commit"

inherit pypi setuptools3
SRC_URI[sha256sum] = "499fe450cc9d42e9d58e606262795ecb64dd05438943c62b66f6a8673da30b16"

RDEPENDS:${PN} = " \
	python3-cfgv \
	python3-identify \
	python3-nodeenv \
	python3-pyyaml \
	python3-virtualenv \
"
