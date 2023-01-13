require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "e9ba9d8ed638733c9e95664470b71d624a6def149e2db6cc52c1aca5a6a2df1d"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
