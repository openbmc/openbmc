SUMMARY = "CircuitPython APIs for non-CircuitPython versions of Python such as CPython on Linux and MicroPython."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Blinka"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fccd531dce4b989c05173925f0bbb76c"

SRC_URI[sha256sum] = "0358f02840f91127246d9c5fa6d49660e2ab1134cc4e56fd5d88ce4c39a50942"

PYPI_PACKAGE = "adafruit_blinka"

inherit pypi python_setuptools_build_meta

DEPENDS += "\
    python3-setuptools-scm-native \
    python3-wheel-native \
"

do_install:append() {
# it ships ./bcm283x/pulseio/libgpiod_pulsein which is a prebuilt
# 32bit binary therefore we should make this specific to 32bit rpi machines (based on bcm283x) only
    if [ ${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '1', '0', d)} = "0" ]; then
        rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/adafruit_blinka/microcontroller/bcm283x
    fi

    # Remove Amlogic as it is not related to Raspberry Pi.
    # When building for raspberrypi5 (AArch64) fixes:
    # QA Issue: Architecture did not match (ARM, expected AArch64)
    rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/adafruit_blinka/microcontroller/amlogic
}

RDEPENDS:${PN} += " \
    libgpiod \
    python3-adafruit-platformdetect \
    python3-adafruit-pureio \
    python3-core \
    python3-lgpio \
"

RDEPENDS:${PN}:append:rpi = " rpi-gpio"

COMPATIBLE_HOST:libc-musl:class-target = "null"

