require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "50163324834edd0c9ce3e4512ded3e221c969086e10fdd5d3fdcaadac5e24a78"

FILES_${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
