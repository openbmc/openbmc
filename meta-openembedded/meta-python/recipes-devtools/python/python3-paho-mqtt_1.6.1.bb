SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8e5f264c6988aec56808a3a11e77b913 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"
SRCNAME = "paho-mqtt"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2a8291c81623aec00372b5a85558a372c747cbca8e9934dfe218638b8eefc26f"

DEPENDS += "python3-pytest-runner-native"

do_install:append() {
        install -d -m0755 ${D}${datadir}/${BPN}/examples
        cp --preserve=mode,timestamps -R ${S}/examples/* ${D}${datadir}/${BPN}/examples
}

PACKAGES =+ "${PN}-examples"

RDEPENDS:${PN}-examples += "${PN} python3-core"

FILES:${PN}-examples = "${datadir}/${BPN}/examples"

RDEPENDS:${PN} = "\
    python3-io \
    python3-logging \
    python3-math \
    python3-netclient \
    python3-threading \
"

BBCLASSEXTEND = "native nativesdk"
