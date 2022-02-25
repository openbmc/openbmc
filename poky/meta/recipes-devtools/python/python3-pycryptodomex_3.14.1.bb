require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "2ce76ed0081fd6ac8c74edc75b9d14eca2064173af79843c24fa62573263c1f2"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
