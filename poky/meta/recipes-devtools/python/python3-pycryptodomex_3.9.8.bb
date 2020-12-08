require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "48cc2cfc251f04a6142badeb666d1ff49ca6fdfc303fd72579f62b768aaa52b9"

FILES_${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
