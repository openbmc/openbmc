require python-pycryptodome.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "09081666ffc599976c0b8b29caf2cf82212c0f05bed233c8f8ed53e4f6e1d356"

CVE_PRODUCT = "pycryptodome:pycryptodome pycryptodome:pycryptodomex"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
