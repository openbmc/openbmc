SUMMARY = "Open-source IoT platform for data collection, processing, visualization, and device management"
DESCRIPTION = "\
The Thingsboard IoT Gateway is an open-source solution that allows you \
to integrate devices connected to legacy and third-party systems with Thingsboard."
HOMEPAGE = "https://thingsboard.io/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI[md5sum] = "469c8b5cd1c16c20ef40f0a97a3a0fda"
SRC_URI[sha256sum] = "b328f4e315c3541ac80a4931974a34a81afe4d1f382f48e8604669a55816c0d7"

inherit pypi setuptools3

PYPI_PACKAGE = "thingsboard-gateway"

RDEPENDS_${PN} += " python3-jsonpath-rw \
                    python3-regex \
                    python3-paho-mqtt \
                    python3-pyyaml \
                    python3-simplejson \
                    python3-requests \
                    python3-pip \
                    python3-pyrsistent \
"

SRC_URI += "file://bacnet.json \
            file://ble.json \
            file://can.json \
            file://custom_serial.json \
            file://modbus.json \
            file://modbus_serial.json \
            file://mqtt.json \
            file://opcua.json \
            file://odbc.json \
            file://request.json \
            file://rest.json \
            file://snmp.json \
            file://tb_gateway.yaml \
            file://logs.conf \
            file://thingsboard-gateway.service \
            "


inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "thingsboard-gateway.service"

FILES_${PN} += "/etc \
                /lib \
                /usr \
"

do_install_append(){

    install -d ${D}${sysconfdir}/thingsboard-gateway/config

    for file in $(find ${WORKDIR} -maxdepth 1 -type f -name *.json); do
        install -m 0644 "$file" ${D}${sysconfdir}/thingsboard-gateway/config
    done

    install -m 0644 ${WORKDIR}/tb_gateway.yaml ${D}${sysconfdir}/thingsboard-gateway/config
    install -m 0644 ${WORKDIR}/logs.conf ${D}${sysconfdir}/thingsboard-gateway/config

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/thingsboard-gateway.service     ${D}${systemd_system_unitdir}/thingsboard-gateway.service
}