SUMMARY = "Open source MQTT implementation"
DESCRIPTION = "Mosquitto is an open source (Eclipse licensed) message broker \
that implements the MQ Telemetry Transport protocol version 3.1, 3.1.1 and \
5, providing both an MQTT broker and several command-line clients. MQTT \
provides a lightweight method of carrying out messaging using a \
publish/subscribe model. "
HOMEPAGE = "http://mosquitto.org/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | EDL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ca9a8f366c6babf593e374d0d7d58749 \
                    file://edl-v10;md5=c09f121939f063aeb5235972be8c722c \
                    file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
                    file://notice.html;md5=541f8f37af492858dab8d2c1b69ede69 \
"
DEPENDS = "uthash cjson dlt-daemon"

SRC_URI = "http://mosquitto.org/files/source/mosquitto-${PV}.tar.gz \
           file://mosquitto.init \
           file://1571.patch \
"

SRC_URI[md5sum] = "542c30f32c4473b7c50dc2e4a1424cf6"
SRC_URI[sha256sum] = "b15da8fc4edcb91d554e1259e220ea0173ef639ceaa4b465e06feb7e125b84bf"

inherit systemd update-rc.d useradd cmake

PACKAGECONFIG ??= "ssl dlt websockets \
                  ${@bb.utils.filter('DISTRO_FEATURES','systemd', d)} \
                  "

PACKAGECONFIG[manpages] = "-DDOCUMENTATION=ON,-DDOCUMENTATION=OFF,libxslt-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[dns-srv] = "-DWITH_SRV=ON,-DWITH_SRV=OFF,c-ares"
PACKAGECONFIG[ssl] = "-DWITH_TLS=ON -DWITH_TLS_PSK=ON -DWITH_EC=ON,-DWITH_TLS=OFF -DWITH_TLS_PSK=OFF -DWITH_EC=OFF,openssl"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF,systemd"
PACKAGECONFIG[websockets] = "-DWITH_WEBSOCKETS=ON,-DWITH_WEBSOCKETS=OFF,libwebsockets"
PACKAGECONFIG[dlt] = "-DWITH_DLT=ON,-DWITH_DLT=OFF,dlt-daemon"

EXTRA_OECMAKE = " \
    -DWITH_BUNDLED_DEPS=OFF \
    -DWITH_ADNS=ON \
"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/service/systemd/mosquitto.service.notify ${D}${systemd_unitdir}/system/mosquitto.service

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
               ${bindir}/mosquitto_ctrl \
               ${libdir}/mosquitto_dynamic_security.so \
               ${sysconfdir}/mosquitto \
               ${sysconfdir}/init.d \
               ${systemd_unitdir}/system/mosquitto.service \
"

CONFFILES_${PN} += "${sysconfdir}/mosquitto/mosquitto.conf"

FILES_libmosquitto1 = "${libdir}/libmosquitto.so.*"

FILES_libmosquittopp1 = "${libdir}/libmosquittopp.so.*"

FILES_${PN}-clients = "${bindir}/mosquitto_pub \
                       ${bindir}/mosquitto_sub \
                       ${bindir}/mosquitto_rr \
"

FILES_${PN}-examples = "${sysconfdir}/mosquitto/*.example"

SYSTEMD_SERVICE_${PN} = "mosquitto.service"

INITSCRIPT_NAME = "mosquitto"
INITSCRIPT_PARAMS = "defaults 30"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --no-create-home --shell /bin/false \
                       --user-group mosquitto"

BBCLASSEXTEND += "native nativesdk"
