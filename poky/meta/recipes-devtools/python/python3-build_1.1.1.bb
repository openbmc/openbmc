SUMMARY = "A simple, correct PEP517 package builder"
HOMEPAGE = "https://github.com/pypa/build"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=310439af287b0fb4780b2ad6907c256c"

SRC_URI[sha256sum] = "8eea65bb45b1aac2e734ba2cc8dad3a6d97d97901a395bd0ed3e7b46953d2a31"

inherit pypi python_flit_core

DEPENDS += "python3-pyproject-hooks-native"

DEPENDS:remove:class-native = "python3-build-native"

# Skip dependencies as we're doing a minimal build to bootstrap
PEP517_BUILD_OPTS:class-native = "--skip-dependency-check"

do_compile:prepend:class-native() {
    export PYTHONPATH="${S}/src"
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
