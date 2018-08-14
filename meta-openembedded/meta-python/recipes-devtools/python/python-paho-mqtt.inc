SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eb48c6ee2cb9f5b8b9fe75e6f817bdfc \
                    file://epl-v10;md5=8d383c379e91d20ba18a52c3e7d3a979 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"
SRCNAME = "paho-mqtt"

inherit pypi
SRC_URI[md5sum] = "2cc27d8b369700b1fc99325add0dadd2"
SRC_URI[sha256sum] = "31911f6031de306c27ed79dc77b690d7c55b0dcb0f0434ca34ec6361d0371122"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-threading \
"
