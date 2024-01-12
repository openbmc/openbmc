require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "0b7154aff2272962355f8941fd514104a88cb29db2d8f43a29af900d6398eb1c"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
