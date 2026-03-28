SUMMARY = "Download, build, install, upgrade, and uninstall Python packages"
HOMEPAGE = "https://pypi.org/project/setuptools"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=141643e11c48898150daa83802dbc65f"

inherit pypi python_setuptools_build_meta

CVE_PRODUCT = "python3-setuptools python:setuptools"

SRC_URI += " \
            file://0001-_distutils-sysconfig.py-make-it-possible-to-substite.patch"

SRC_URI[sha256sum] = "7d872682c5d01cfde07da7bccc7b65469d3dca203318515ada1de5eda35efbf9"

do_install:append() {
	# setuptools ships Windows launcher executables (cli*.exe, gui*.exe).
	# Keep them only when building for a Windows (mingw) host.
	case "${HOST_OS}" in
		mingw32|mingw64) ;;
		*) rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/setuptools/*.exe ;;
	esac
}

DEPENDS += "python3"

RDEPENDS:${PN} = "\
    python3-compile \
    python3-compression \
    python3-ctypes \
    python3-email \
    python3-html \
    python3-json \
    python3-netserver \
    python3-numbers \
    python3-pickle \
    python3-pkgutil \
    python3-plistlib \
    python3-shell \
    python3-stringold \
    python3-threading \
    python3-unittest \
    python3-unixadmin \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
