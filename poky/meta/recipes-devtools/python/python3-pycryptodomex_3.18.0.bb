require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "3e3ecb5fe979e7c1bb0027e518340acf7ee60415d79295e5251d13c68dde576e"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
