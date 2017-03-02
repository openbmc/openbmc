SUMMARY = "Phosphor DBUS to REST WSGI Application"
DESCRIPTION = "Phosphor DBUS to REST WSGI Application."
PR = "r1"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools
inherit obmc-phosphor-discovery-service

require phosphor-rest.inc

RRECOMMENDS_${PN} += "virtual-obmc-wsgihost"

RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        phosphor-mapper \
        python-bottle \
        python-spwd \
        pyphosphor-utils \
        pyphosphor-dbus \
        pyphosphor-wsgi-apps-ns \
        "

S = "${WORKDIR}/git/module"
SYSTEMD_SERVICE_${PN} = ""
SYSTEMD_OVERRIDE_${PN} += "rest-dbus.conf:obmc-mapper.target.d/rest-dbus.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/wsgi_app"
REGISTERED_SERVICES_${PN} += "phosphor_rest:tcp:443"
