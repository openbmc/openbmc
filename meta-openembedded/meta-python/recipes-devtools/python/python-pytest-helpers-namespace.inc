DESCRIPTION = "This plugin does not provide any helpers to pytest, it does, however, provide a helpers namespace in pytest which enables you to register helper functions in your conftest.py to be used within your tests without having to import them."
HOMEPAGE = "https://github.com/saltstack/pytest-helpers-namespace"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=71fac4dfeee99c39c51f617e9486237c"

SRC_URI[md5sum] = "80fe3e515f640b1459709b539f6b857e"
SRC_URI[sha256sum] = "d8bdb017c0f7271699c08850b6b12e1ce9e52913cbd4461b5febd21082a29113"

inherit pypi
