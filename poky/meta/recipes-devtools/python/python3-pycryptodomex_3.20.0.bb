require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "7a710b79baddd65b806402e14766c721aee8fb83381769c27920f26476276c1e"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
