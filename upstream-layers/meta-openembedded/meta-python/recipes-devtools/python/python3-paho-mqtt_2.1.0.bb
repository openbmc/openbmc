SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 OR LicenseRef-EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8e5f264c6988aec56808a3a11e77b913 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"

inherit pypi python_hatchling

PYPI_PACKAGE = "paho_mqtt"

SRC_URI[sha256sum] = "12d6e7511d4137555a3f6ea167ae846af2c7357b10bc6fa4f7c3968fc1723834"

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
