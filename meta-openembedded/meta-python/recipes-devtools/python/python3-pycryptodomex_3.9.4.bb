require python-pycryptodome.inc
inherit setuptools3

SRC_URI[md5sum] = "46ba513d95b6e323734074d960a7d57b"
SRC_URI[sha256sum] = "22d970cee5c096b9123415e183ae03702b2cd4d3ba3f0ced25c4e1aba3967167"

FILES_${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
