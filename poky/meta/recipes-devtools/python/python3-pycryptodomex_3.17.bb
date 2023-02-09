require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "0af93aad8d62e810247beedef0261c148790c52f3cd33643791cc6396dd217c1"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
