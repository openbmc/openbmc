SUMMARY = "Paho MQTT - C libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-1.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
        file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
        file://epl-v10;md5=659c8e92a40b6df1d9e3dccf5ae45a08 \
        file://notice.html;md5=a00d6f9ab542be7babc2d8b80d5d2a4c \
        file://about.html;md5=dcde438d73cf42393da9d40fabc0c9bc \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.c;protocol=http"

SRCREV = "09fe0744e02f317b907e96dd5afcc02224ddbb85"

DEPENDS = "openssl"

S = "${WORKDIR}/git"

TARGET_CC_ARCH += "${LDFLAGS}"

do_install() {
    install -d ${D}${libdir}
    oe_libinstall -C build/output -so libpaho-mqtt3a ${D}${libdir}
    oe_libinstall -C build/output -so libpaho-mqtt3as ${D}${libdir}
    oe_libinstall -C build/output -so libpaho-mqtt3c  ${D}${libdir}
    oe_libinstall -C build/output -so libpaho-mqtt3cs ${D}${libdir}
    install -d ${D}${includedir}
    install -m 644 src/MQTTAsync.h ${D}${includedir}
    install -m 644 src/MQTTClient.h ${D}${includedir}
    install -m 644 src/MQTTClientPersistence.h ${D}${includedir}
}

