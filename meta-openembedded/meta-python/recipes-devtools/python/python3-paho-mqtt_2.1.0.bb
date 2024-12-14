SUMMARY = "MQTT version 3.1/3.1.1 client library"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8e5f264c6988aec56808a3a11e77b913 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
"
SRCNAME = "paho-mqtt"

inherit pypi python_hatchling python_setuptools_build_meta

SRC_URI[sha256sum] = "12d6e7511d4137555a3f6ea167ae846af2c7357b10bc6fa4f7c3968fc1723834"

PYPI_SRC_URI = "https://files.pythonhosted.org/packages/39/15/0a6214e76d4d32e7f663b109cf71fb22561c2be0f701d67f93950cd40542/paho_mqtt-${PV}.tar.gz"

S = "${WORKDIR}/paho_mqtt-${PV}"

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
