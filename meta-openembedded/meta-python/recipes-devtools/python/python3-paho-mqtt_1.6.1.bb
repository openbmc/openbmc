SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8e5f264c6988aec56808a3a11e77b913 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"
SRCNAME = "paho-mqtt"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2a8291c81623aec00372b5a85558a372c747cbca8e9934dfe218638b8eefc26f"

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

do_install:append() {
        install -d -m0755 ${D}${datadir}/${BPN}/examples
        cp --preserve=mode,timestamps -R ${S}/examples/* ${D}${datadir}/${BPN}/examples
}

PACKAGES =+ "${PN}-examples"

RDEPENDS:${PN}-examples += "${PN} ${PYTHON_PN}-core"

FILES:${PN}-examples = "${datadir}/${BPN}/examples"

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-math \
    ${PYTHON_PN}-netclient \
    ${PYTHON_PN}-threading \
"

BBCLASSEXTEND = "native nativesdk"
