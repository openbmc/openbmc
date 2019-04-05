SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eb48c6ee2cb9f5b8b9fe75e6f817bdfc \
                    file://epl-v10;md5=8d383c379e91d20ba18a52c3e7d3a979 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"
SRCNAME = "paho-mqtt"

inherit pypi

SRC_URI[md5sum] = "e3ac29cd5dc247a01083a2a8f3fddd08"
SRC_URI[sha256sum] = "e440a052b46d222e184be3be38676378722072fcd4dfd2c8f509fb861a7b0b79"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-threading \
"
