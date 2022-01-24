require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "922e9dac0166e4617e5c7980d2cff6912a6eb5cb5c13e7ece222438650bd7f66"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
