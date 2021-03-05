require python-pycryptodome.inc
inherit setuptools3

SRC_URI[sha256sum] = "541cd3e3e252fb19a7b48f420b798b53483302b7fe4d9954c947605d0a263d62"

FILES_${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
