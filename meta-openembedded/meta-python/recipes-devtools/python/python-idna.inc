SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=782775b32f96098512e283fb5d4546cd"

SRC_URI[md5sum] = "0e5bb69018ddef1b9d95f681182be82c"
SRC_URI[sha256sum] = "684a38a6f903c1d71d6d5fac066b58d7768af4de2b832e426ec79c30daa94a16"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-codecs \
"

# Remove bundled egg-info
do_compile_prepend() {
    rm -rf ${S}/idna.egg-info
}

BBCLASSEXTEND = "native nativesdk"
