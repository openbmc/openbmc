DESCRIPTION = "This plugin does not provide any helpers to pytest, it does, however, provide a helpers namespace in pytest which enables you to register helper functions in your conftest.py to be used within your tests without having to import them."
HOMEPAGE = "https://github.com/saltstack/pytest-helpers-namespace"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=440a4cdb311cd7ad181efb4cba06d562"

SRC_URI[sha256sum] = "183524e3db4e2a1fea92e0ca3662a624ba44c9f3568da15679d7535ba6838a6a"

inherit pypi setuptools3

# Workaround for network access issue during compile step
# this needs to be fixed in the recipes buildsystem to move
# this such that it can be accomplished during do_fetch task
do_compile[network] = "1"

DEPENDS += "\
    ${PYTHON_PN}-wheel-native \
    ${PYTHON_PN}-pip-native \
"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"
