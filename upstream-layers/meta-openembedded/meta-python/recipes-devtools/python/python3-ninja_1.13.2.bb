SUMMARY = "Ninja is a small build system with a focus on speed"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_Apache_20;md5=19cbd64715b51267a47bf3750cc6a8a5"

PYPI_ARCHIVE_NAME_PREFIX = "pypi-"

inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "525bfa3fc88aa30a4467df270fd5be6f9fcae8061d54d4df74ea1dc5abd5a975"

SRC_URI += "file://no-scikit-build.patch "

DEPENDS += "python3-setuptools-scm-native"

do_install:append () {
	rm -rf ${D}${bindir}
	install -m 0644 ${S}/ninja-upstream/misc/ninja_syntax.py ${D}${PYTHON_SITEPACKAGES_DIR}/ninja/ninja_syntax.py
}

do_configure:prepend() {
	echo 'version = "${PV}"' > ${S}/src/ninja/_version.py
}

RDEPENDS:${PN} = " \
    ninja \
    python3-io \
    python3-json \
    python3-ninja-syntax \
"

BBCLASSEXTEND = "native nativesdk"
