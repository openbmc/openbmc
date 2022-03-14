SUMMARY = "CircuitPython APIs for non-CircuitPython versions of Python such as CPython on Linux and MicroPython."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Blinka"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=660e614bc7efb0697cc793d8a22a55c2"

SRC_URI = "git://github.com/adafruit/Adafruit_Blinka.git;branch=main;protocol=https"
SRCREV = "dc688f354fe779c9267c208b99f310af87e79272"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

do_install:append() {
# it ships ./bcm283x/pulseio/libgpiod_pulsein which is a prebuilt
# 32bit binary therefore we should make this specific to 32bit rpi machines (based on bcm283x) only
    if [ ${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '1', '0', d)} = "0" ]; then
        rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/adafruit_blinka/microcontroller/bcm283x
    fi
}

RDEPENDS:${PN} += " \
    libgpiod \
    python3-adafruit-platformdetect \
    python3-adafruit-pureio \
    python3-core \
"

RDEPENDS:${PN}:append:rpi = " rpi-gpio"
