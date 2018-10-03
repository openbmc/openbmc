SUMMARY = "Phosphor DBUS to REST WSGI Application"
DESCRIPTION = "Phosphor DBUS to REST WSGI Application."
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools
inherit obmc-phosphor-discovery-service

require phosphor-rest.inc

RRECOMMENDS_${PN} += " \
        virtual-obmc-wsgihost \
        python-gevent-websocket \
        "

RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        phosphor-mapper \
        python-bottle \
        python-spwd \
        pyphosphor-utils \
        pyphosphor-dbus \
        pyphosphor-wsgi-apps-ns \
        pamela \
        jsnbd \
        "
SRC_URI += "file://url_config.json \
           "

FILES_${PN}_append = " ${datadir}/rest-dbus/url_config.json"

S = "${WORKDIR}/git/module"
SYSTEMD_SERVICE_${PN} = ""
SYSTEMD_OVERRIDE_${PN} += "rest-dbus.conf:obmc-mapper.target.d/rest-dbus.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/wsgi_app"
REGISTERED_SERVICES_${PN} += "phosphor_rest:tcp:443"

do_install_append(){
    install -d ${D}${datadir}/rest-dbus
    install -m 0644 -D ${WORKDIR}/url_config.json \
        ${D}${datadir}/rest-dbus/url_config.json
}
