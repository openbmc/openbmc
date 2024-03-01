DESCRIPTION = "SDK for connecting to AWS IoT using Python."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python"
LICENSE = "Apache-2.0 & (EPL-1.0 | EDL-1.0)"
LICENSE:${PN}-examples = "Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE.txt;md5=9ac49901b833e769c7d6f21e8dbd7b30 \
    file://AWSIoTPythonSDK/core/protocol/paho/client.py;endline=14;md5=5a3c8a1a4bb71bd934f450ecff972ad9 \
"
SRCREV = "0ea1a2d013529839fc1e7448d19dadff25d581b4"
SRC_URI = "git://github.com/aws/aws-iot-device-sdk-python;branch=master;protocol=https \
           file://0001-setup.py-Use-setuptools-instead-of-distutils.patch \
           "

S = "${WORKDIR}/git"

inherit setuptools3

PYPI_PACKAGE = "AWSIoTPythonSDK"

do_install:append() {
        install -d -m0755 ${D}${datadir}/${BPN}/examples
        cp --preserve=mode,timestamps -R ${S}/samples/* ${D}${datadir}/${BPN}/examples
        # this requires the full blown AWS Python SDK
        rm -r ${D}${datadir}/${BPN}/examples/basicPubSub
}

PACKAGES =+ "${PN}-examples"

RDEPENDS:${PN} += " \
    python3-crypt \
    python3-datetime \
    python3-io \
    python3-json \
    python3-logging \
    python3-math \
    python3-netclient \
    python3-numbers \
    python3-threading \
"
RDEPENDS:${PN}-examples += "${PN}"

FILES:${PN}-examples = "${datadir}/${BPN}/examples"

BBCLASSEXTEND = "native nativesdk"
