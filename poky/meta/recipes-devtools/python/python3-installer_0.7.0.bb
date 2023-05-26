SUMMARY = "Library and tool for installing Python wheels"
DESCRIPTION = "A low-level library for installing a Python package from a wheel distribution."
HOMEPAGE = "https://installer.readthedocs.io/"
BUGTRACKER = "https://github.com/pypa/installer/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5038641aec7a77451e31da828ebfae00"

SRC_URI += "file://interpreter.patch"

SRC_URI[sha256sum] = "a26d3e3116289bb08216e0d0f7d925fcef0b0194eedfa0c944bcaaa106c4b631"

inherit pypi python_flit_core

# Bootstrap the native build
DEPENDS:remove:class-native = "python3-build-native python3-installer-native"

RDEPENDS:${PN} += " \
    python3-compile \
    python3-compression \
    python3-netclient \
"

INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"

do_compile:class-native () {
    python_flit_core_do_manual_build
}

do_install:prepend:class-native() {
    export PYTHONPATH="${S}/src"
}

BBCLASSEXTEND = "native nativesdk"
