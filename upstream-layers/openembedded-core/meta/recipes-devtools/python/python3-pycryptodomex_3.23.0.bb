require python-pycryptodome.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "71909758f010c82bc99b0abf4ea12012c98962fbf0583c2164f8b84533c2e4da"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
