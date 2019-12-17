DESCRIPTION = "SDK for connecting to AWS IoT using Python."
HOMEPAGE = "https://github.com/aws/aws-iot-device-sdk-python"
LICENSE = "Apache-2.0 & (EPL-1.0 | EDL-1.0)"
LICENSE_${PN}-examples = "Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://LICENSE.txt;md5=9ac49901b833e769c7d6f21e8dbd7b30 \
    file://AWSIoTPythonSDK/core/protocol/paho/client.py;endline=14;md5=5a3c8a1a4bb71bd934f450ecff972ad9 \
"

SRC_URI[md5sum] = "2b3df629827d8bc18ca4d927b4885a17"
SRC_URI[sha256sum] = "7e67c8cfeee8a1c8fd251bcc418de9d4dca697753565c9a9e614525179d9555d"

inherit pypi

PYPI_PACKAGE = "AWSIoTPythonSDK"

do_install_append() {
        install -d -m0755 ${D}${datadir}/${BPN}/examples
        cp --preserve=mode,timestamps -R ${S}/samples/* ${D}${datadir}/${BPN}/examples
        # this requires the full blown AWS Python SDK
        rm -r ${D}${datadir}/${BPN}/examples/basicPubSub
}

PACKAGES =+ "${PN}-examples"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-crypt \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-threading \
"
RDEPENDS_${PN}-examples += "${PN}"

FILES_${PN}-examples = "${datadir}/${BPN}/examples"

BBCLASSEXTEND = "native nativesdk"
