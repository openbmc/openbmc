LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=218947f77e8cb8e2fa02918dc41c50d0"

SRC_URI = "git://github.com/DaveGamble/cJSON.git"

PV = "1.7.6+git${SRCPV}"
SRCREV = "cbc05de76fbd4dfff17b5626d5cfe9ec922b1f4a"

S = "${WORKDIR}/git"

inherit cmake

FILES_${PN}-dev += "${libdir}/cmake/cJSON/*"

EXTRA_OECMAKE = "-DENABLE_CUSTOM_COMPILER_FLAGS=OFF -DENABLE_TARGET_EXPORT=OFF"
