SUMMARY = "Library and tool for installing Python wheels"
DESCRIPTION = "A low-level library for installing a Python package from a wheel distribution."
HOMEPAGE = "https://installer.readthedocs.io/"
BUGTRACKER = "https://github.com/pypa/installer/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5038641aec7a77451e31da828ebfae00"

SRC_URI += "file://interpreter.patch"

SRC_URI[sha256sum] = "f3bd36cd261b440a88a1190b1becca0578fee90b4b62decc796932fdd5ae8839"

inherit pypi python_flit_core

# Bootstrap the native build
DEPENDS:remove:class-native = "python3-build-native python3-installer-native"

INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"

do_compile:class-native () {
    python_flit_core_do_manual_build
}

do_install:prepend:class-native() {
    export PYTHONPATH="${S}/src"
}

BBCLASSEXTEND = "native nativesdk"
