SUMMARY = "Open source MQTT implementation"
DESCRIPTION = "Mosquitto is an open source (Eclipse licensed) message broker \
that implements the MQ Telemetry Transport protocol version 3.1, 3.1.1 and \
5, providing both an MQTT broker and several command-line clients. MQTT \
provides a lightweight method of carrying out messaging using a \
publish/subscribe model. "
HOMEPAGE = "http://mosquitto.org/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6d64fd27a4071b14ef656a0e8a4f4cf \
                    file://edl-v10;md5=9f6accb1afcb570f8be65039e2fcd49e \
                    file://epl-v20;md5=2dd765ca47a05140be15ebafddbeadfe \
                    file://NOTICE.md;md5=611234becddb76dca161d4ffce7ab420 \
"
DEPENDS = "uthash cjson"

SRC_URI = "http://mosquitto.org/files/source/mosquitto-${PV}.tar.gz \
           file://mosquitto.init \
"

SRC_URI[sha256sum] = "fd905380691ac65ea5a93779e8214941829e3d6e038d5edff9eac5fd74cbed02"

inherit systemd update-rc.d useradd cmake pkgconfig

PACKAGECONFIG ??= "ssl websockets \
                  ${@bb.utils.filter('DISTRO_FEATURES','systemd', d)} \
                  "

PACKAGECONFIG[manpages] = "-DWITH_DOCS=ON,-DWITH_DOCS=OFF,libxslt-native docbook-xsl-stylesheets-native"
PACKAGECONFIG[dns-srv] = "-DWITH_SRV=ON,-DWITH_SRV=OFF,c-ares"
PACKAGECONFIG[ssl] = "-DWITH_TLS=ON -DWITH_TLS_PSK=ON,-DWITH_TLS=OFF -DWITH_TLS_PSK=OFF,openssl"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF,systemd"
PACKAGECONFIG[websockets] = "-DWITH_WEBSOCKETS=ON -DWITH_WEBSOCKETS_BUILTIN=OFF,-DWITH_WEBSOCKETS=OFF,libwebsockets"
PACKAGECONFIG[dlt] = "-DWITH_DLT=ON,-DWITH_DLT=OFF,dlt-daemon"
PACKAGECONFIG[http-api] = "-DWITH_HTTP_API=ON,-DWITH_HTTP_API=OFF,libmicrohttpd"
PACKAGECONFIG[persist-sqlite] = "-DWITH_PLUGIN_PERSIST_SQLITE=ON,-DWITH_PLUGIN_PERSIST_SQLITE=OFF,sqlite3"
PACKAGECONFIG[ctrl-shell] = "-DWITH_CTRL_SHELL=ON,-DWITH_CTRL_SHELL=OFF,libedit"

# Disable building/running tests to avoid GoogleTest discovery executing target binaries
# during cross-compilation (Yocto). This prevents CMake from calling gtest_discover_tests().
# See: Yocto + GTest cross-compile issues and gtest_discover_tests execution failures.
# (Refs: SO example and googletest issue)
EXTRA_OECMAKE = " \
    -DWITH_BUNDLED_DEPS=OFF \
    -DWITH_ADNS=OFF \
    -DWITH_TESTS=OFF \
"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${S}/service/systemd/mosquitto.service.notify ${D}${systemd_unitdir}/system/mosquitto.service

    install -d ${D}${sysconfdir}/init.d/
    install -m 0755 ${UNPACKDIR}/mosquitto.init ${D}${sysconfdir}/init.d/mosquitto
    sed -i -e 's,@SBINDIR@,${sbindir},g' \
        -e 's,@BASE_SBINDIR@,${base_sbindir},g' \
        -e 's,@LOCALSTATEDIR@,${localstatedir},g' \
        -e 's,@SYSCONFDIR@,${sysconfdir},g' \
        ${D}${sysconfdir}/init.d/mosquitto
}

PACKAGE_BEFORE_PN = "libmosquitto1 libmosquittopp1 ${PN}-clients ${PN}-examples"

FILES:${PN} += " \
    ${libdir}/mosquitto_acl_file.so \
    ${libdir}/mosquitto_dynamic_security.so \
    ${libdir}/mosquitto_password_file.so \
    ${libdir}/mosquitto_sparkplug_aware.so \
    ${systemd_unitdir}/system/mosquitto.service \
"

CONFFILES:${PN} += "${sysconfdir}/mosquitto/mosquitto.conf"

FILES:libmosquitto1 = "${libdir}/libmosquitto.so.*"

FILES:libmosquittopp1 = "${libdir}/libmosquittopp.so.*"

FILES:${PN}-clients = "${bindir}/mosquitto_pub \
                       ${bindir}/mosquitto_sub \
                       ${bindir}/mosquitto_rr \
"

FILES:${PN}-examples = "${sysconfdir}/mosquitto/*.example"

SYSTEMD_SERVICE:${PN} = "mosquitto.service"

INITSCRIPT_NAME = "mosquitto"
INITSCRIPT_PARAMS = "defaults 30"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --shell /bin/false \
                       --user-group mosquitto"

BBCLASSEXTEND = "native"
