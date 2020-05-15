SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=cf36c8682cc154d2d4aa57bd6246b9a1"

SRC_URI[md5sum] = "870b8b80267f00f0faf1b7ba4bdbf14e"
SRC_URI[sha256sum] = "7588d1c14ae4c77d74036e8c22ff447b26d0fde8f007354fd48a7814db15b7cb"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-codecs \
"

# Remove bundled egg-info
do_compile_prepend() {
    rm -rf ${S}/idna.egg-info
}

BBCLASSEXTEND = "native nativesdk"
