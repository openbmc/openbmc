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
DEPENDS = "uthash"

SRC_URI = "http://mosquitto.org/files/source/mosquitto-${PV}.tar.gz \
           file://0002-uthash-remove-in-tree-version.patch \
           file://mosquitto.init \
"

SRC_URI[md5sum] = "f98c99998a36a234f3a9d9b402b991db"
SRC_URI[sha256sum] = "8557bc7ae34dfaf32a0fb56d2491b7a7f731269c88337227233013502df4d5b0"

inherit systemd update-rc.d useradd

PACKAGECONFIG ??= "ssl uuid \
                  ${@bb.utils.filter('DISTRO_FEATURES','systemd', d)} \
                  "

PACKAGECONFIG[dns-srv] = "WITH_SRV=yes,WITH_SRV=no,c-ares"
PACKAGECONFIG[ssl] = "WITH_TLS=yes WITH_TLS_PSK=yes,WITH_TLS=no WITH_TLS_PSK=no,openssl"
PACKAGECONFIG[uuid] = "WITH_UUID=yes,WITH_UUID=no,util-linux"
PACKAGECONFIG[systemd] = "WITH_SYSTEMD=yes,WITH_SYSTEMD=no,systemd"
PACKAGECONFIG[websockets] = "WITH_WEBSOCKETS=yes,WITH_WEBSOCKETS=no,libwebsockets"

EXTRA_OEMAKE = " \
    prefix=${prefix} \
    mandir=${mandir} \
    localedir=${localedir} \
    ${PACKAGECONFIG_CONFARGS} \
    STRIP=/bin/true \
    WITH_DOCS=no \
"

export LIB_SUFFIX = "${@d.getVar('baselib', True).replace('lib', '')}"

do_install() {
    oe_runmake 'DESTDIR=${D}' install

    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/service/systemd/mosquitto.service.notify ${D}${systemd_unitdir}/system/mosquitto.service

    install -d ${D}${sysconfdir}/mosquitto
    install -m 0644 ${D}${sysconfdir}/mosquitto/mosquitto.conf.example \
                    ${D}${sysconfdir}/mosquitto/mosquitto.conf

    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${WORKDIR}/mosquitto.init ${D}${sysconfdir}/init.d/mosquitto
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${sysconfdir}/init.d/mosquitto
}

PACKAGES += "libmosquitto1 libmosquittopp1 ${PN}-clients"

PACKAGE_BEFORE_PN = "${PN}-examples"

FILES_${PN} = "${sbindir}/mosquitto \
               ${bindir}/mosquitto_passwd \
               ${sysconfdir}/mosquitto \
               ${sysconfdir}/init.d \
               ${systemd_unitdir}/system/mosquitto.service \
"

CONFFILES_${PN} += "${sysconfdir}/mosquitto/mosquitto.conf"

FILES_libmosquitto1 = "${libdir}/libmosquitto.so.1"

FILES_libmosquittopp1 = "${libdir}/libmosquittopp.so.1"

FILES_${PN}-clients = "${bindir}/mosquitto_pub \
                       ${bindir}/mosquitto_sub \
"

FILES_${PN}-examples = "${sysconfdir}/mosquitto/*.example"

SYSTEMD_SERVICE_${PN} = "mosquitto.service"

INITSCRIPT_NAME = "mosquitto"
INITSCRIPT_PARAMS = "defaults 30"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --shell /bin/false \
                       --user-group mosquitto"
