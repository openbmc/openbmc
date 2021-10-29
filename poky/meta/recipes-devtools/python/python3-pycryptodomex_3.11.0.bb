require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "0398366656bb55ebdb1d1d493a7175fc48ade449283086db254ac44c7d318d6d"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
