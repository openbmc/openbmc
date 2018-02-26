SUMMARY = "Open source MQTT v3.1/3.1.1 implemention"
DESCRIPTION = "Mosquitto is an open source (Eclipse licensed) message broker that implements the MQ Telemetry Transport protocol version 3.1 and 3.1.1. MQTT provides a lightweight method of carrying out messaging using a publish/subscribe model. "
HOMEPAGE = "http://mosquitto.org/"
SECTION = "console/network"
LICENSE = "EPL-1.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=62ddc846179e908dc0c8efec4a42ef20 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
                    file://epl-v10;md5=8d383c379e91d20ba18a52c3e7d3a979 \
                    file://notice.html;md5=a00d6f9ab542be7babc2d8b80d5d2a4c \
"

SRC_URI = "http://mosquitto.org/files/source/mosquitto-${PV}.tar.gz \
           file://build.patch \
           file://mosquitto.service \
           file://mosquitto.init \
"

SRC_URI[md5sum] = "6b0966e93f118bc71ad7b61600a6c2d3"
SRC_URI[sha256sum] = "156b1fa731d12baad4b8b22f7b6a8af50ba881fc711b81e9919ec103cf2942d1"

inherit systemd update-rc.d useradd

PACKAGECONFIG ??= "ssl uuid"

PACKAGECONFIG[dns-srv] = ",,c-ares"
PACKAGECONFIG[ssl] = ",,openssl"
PACKAGECONFIG[uuid] = ",,util-linux"
EXTRA_OEMAKE = "${@bb.utils.contains('PACKAGECONFIG', 'dns-srv', 'WITH_SRV=yes', 'WITH_SRV=no', d)} \
                WITH_DOCS=no \
                ${@bb.utils.contains('PACKAGECONFIG', 'ssl', 'WITH_TLS=yes WITH_TLS_PSK=yes', 'WITH_TLS=no WITH_TLS_PSK=no', d)} \
                ${@bb.utils.contains('PACKAGECONFIG', 'uuid', 'WITH_UUID=yes', 'WITH_UUID=no', d)}"

export LIB_SUFFIX="${@d.getVar('baselib', True).replace('lib', '')}"

do_compile() {
    oe_runmake PREFIX=${prefix}
}

do_install() {
    oe_runmake install DESTDIR=${D}
    install -d ${D}${libdir}
    install -m 0644 lib/libmosquitto.a ${D}${libdir}/

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/mosquitto.service ${D}${systemd_unitdir}/system/

    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/mosquitto.init ${D}${sysconfdir}/init.d/mosquitto
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${sysconfdir}/init.d/mosquitto
}

PACKAGES += "libmosquitto1 libmosquittopp1 ${PN}-clients"

FILES_${PN} = "${sbindir}/mosquitto \
               ${bindir}/mosquitto_passwd \
               ${sysconfdir}/mosquitto \
               ${sysconfdir}/init.d \
               ${systemd_unitdir}/system/mosquitto.service \
"

FILES_libmosquitto1 = "${libdir}/libmosquitto.so.1"

FILES_libmosquittopp1 = "${libdir}/libmosquittopp.so.1"

FILES_${PN}-clients = "${bindir}/mosquitto_pub \
                       ${bindir}/mosquitto_sub \
"

SYSTEMD_SERVICE_${PN} = "mosquitto.service"

INITSCRIPT_NAME = "mosquitto"
INITSCRIPT_PARAMS = "defaults 30"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --shell /bin/false \
                       --user-group mosquitto"
