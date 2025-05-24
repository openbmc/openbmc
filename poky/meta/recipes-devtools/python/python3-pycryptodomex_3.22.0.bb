require python-pycryptodome.inc
inherit python_setuptools_build_meta

SRC_URI[sha256sum] = "a1da61bacc22f93a91cbe690e3eb2022a03ab4123690ab16c46abb693a9df63d"

FILES:${PN}-tests = " \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/ \
    ${PYTHON_SITEPACKAGES_DIR}/Cryptodome/SelfTest/__pycache__/ \
"
