SUMMARY = "A simple, correct PEP517 package builder"
HOMEPAGE = "https://github.com/pypa/build"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=310439af287b0fb4780b2ad6907c256c"

SRC_URI[sha256sum] = "d5b71264afdb5951d6704482aac78de887c80691c52b88a9ad195983ca2c9269"

inherit pypi python_flit_core

DEPENDS += "python3-pyproject-hooks-native"

DEPENDS:remove:class-native = "python3-build-native"

# Skip dependencies as we're doing a minimal build to bootstrap
PEP517_BUILD_OPTS:class-native = "--skip-dependency-check"

do_compile:prepend:class-native() {
    export PYTHONPATH="${S}/src"
}

RDEPENDS:${PN} += "python3-packaging python3-pyproject-hooks"

BBCLASSEXTEND = "native nativesdk"
