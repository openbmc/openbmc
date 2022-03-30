SUMMARY = "Library and tool for installing Python wheels"
DESCRIPTION = "A low-level library for installing a Python package from a wheel distribution."
HOMEPAGE = "https://installer.readthedocs.io/"
BUGTRACKER = "https://github.com/pypa/installer/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5038641aec7a77451e31da828ebfae00"

SRC_URI += "file://interpreter.patch"

SRC_URI[sha256sum] = "f970995ec2bb815e2fdaf7977b26b2091e1e386f0f42eafd5ac811953dc5d445"

inherit pypi python_flit_core

DEPENDS:remove:class-native = "python3-installer-native"
DEPENDS:append:class-native = " unzip-native"

do_install:class-native () {
    python_pep517_do_bootstrap_install
}

BBCLASSEXTEND = "native nativesdk"
