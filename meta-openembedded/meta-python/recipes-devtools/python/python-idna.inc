SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=cf36c8682cc154d2d4aa57bd6246b9a1"

SRC_URI[md5sum] = "7a910c706db30d758f377db2762c0f9a"
SRC_URI[sha256sum] = "b307872f855b18632ce0c21c5e45be78c0ea7ae4c15c828c20788b26921eb3f6"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-codecs \
"

# Remove bundled egg-info
do_compile_prepend() {
    rm -rf ${S}/idna.egg-info
}

BBCLASSEXTEND = "native nativesdk"
