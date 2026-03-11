DESCRIPTION = "This plugin does not provide any helpers to pytest, it does, however, provide a helpers namespace in pytest which enables you to register helper functions in your conftest.py to be used within your tests without having to import them."
HOMEPAGE = "https://github.com/saltstack/pytest-helpers-namespace"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=440a4cdb311cd7ad181efb4cba06d562"

SRC_URI[sha256sum] = "792038247e0021beb966a7ea6e3a70ff5fcfba77eb72c6ec8fd6287af871c35b"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native python3-setuptools-declarative-requirements-native"

RDEPENDS:${PN} += " \
    python3-pytest \
"
