SUMMARY = "A simple, correct PEP517 package builder"
HOMEPAGE = "https://github.com/pypa/build"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=310439af287b0fb4780b2ad6907c256c"

SRC_URI += "file://check-valid-dist.patch"

SRC_URI[sha256sum] = "bd2c8afc603e7a2e0ce70e2ea85f0a6d02043bafbd307f5bada0f98669eca5af"

inherit pypi python_flit_core

DEPENDS += "python3-pyproject-hooks-native"

do_compile:class-native() {
    python_flit_core_do_manual_build
}

RDEPENDS:${PN} += " \
    python3-compression \
    python3-difflib \
    python3-ensurepip \
    python3-logging \
    python3-packaging \
    python3-pyproject-hooks \
    python3-tomllib \
    python3-venv \
"

BBCLASSEXTEND = "native nativesdk"
