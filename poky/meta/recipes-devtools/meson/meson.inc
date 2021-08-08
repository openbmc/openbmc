HOMEPAGE = "http://mesonbuild.com"
SUMMARY = "A high performance build system"
DESCRIPTION = "Meson is a build system designed to increase programmer \
productivity. It does this by providing a fast, simple and easy to use \
interface for modern software development tools and practices."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "https://github.com/mesonbuild/meson/releases/download/${PV}/meson-${PV}.tar.gz \
           file://0001-gtkdoc-fix-issues-that-arise-when-cross-compiling.patch \
           file://0003-native_bindir.patch \
           file://0001-python-module-do-not-manipulate-the-environment-when.patch \
           file://disable-rpath-handling.patch \
           file://0001-modules-python.py-do-not-substitute-python-s-install.patch \
           file://gi-target-dep.patch \
           file://0001-Make-CPU-family-warnings-fatal.patch \
           file://0002-Support-building-allarch-recipes-again.patch \
           "
SRC_URI[sha256sum] = "3144a3da662fcf79f1e5602fa929f2821cba4eba28c2c923fe0a7d3e3db04d5d"

UPSTREAM_CHECK_URI = "https://github.com/mesonbuild/meson/releases"
UPSTREAM_CHECK_REGEX = "meson-(?P<pver>\d+(\.\d+)+)\.tar"

inherit setuptools3

RDEPENDS:${PN} = "ninja python3-modules python3-pkg-resources"

FILES:${PN} += "${datadir}/polkit-1"

do_install:append () {
	# As per the same issue in the python recipe itself:
	# Unfortunately the following pyc files are non-deterministc due to 'frozenset'
	# being written without strict ordering, even with PYTHONHASHSEED = 0
	# Upstream is discussing ways to solve the issue properly, until then let's
	# just not install the problematic files.
	# More info: http://benno.id.au/blog/2013/01/15/python-determinism
	rm ${D}${libdir}/python*/site-packages/mesonbuild/dependencies/__pycache__/mpi.cpython*
}
