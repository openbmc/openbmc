require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "af83a554b3f077564229865c45af0791be008ac6469ef0098152139e6bd4b5b6"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
