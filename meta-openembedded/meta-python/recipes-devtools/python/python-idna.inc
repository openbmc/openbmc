SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=782775b32f96098512e283fb5d4546cd"

SRC_URI[md5sum] = "2e9ae0b4a0b26d1747c6127cdb060bc1"
SRC_URI[sha256sum] = "c357b3f628cf53ae2c4c05627ecc484553142ca23264e593d327bcde5e9c3407"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-codecs \
"

# Remove bundled egg-info
do_compile_prepend() {
    rm -rf ${S}/idna.egg-info
}

BBCLASSEXTEND = "native nativesdk"
