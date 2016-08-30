SUMMARY = "Phosphor DBUS to REST WSGI Application"
DESCRIPTION = "Phosphor DBUS to REST WSGI Application."
HOMEPAGE = "http://github.com/openbmc/phosphor-rest-server"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit allarch
inherit obmc-phosphor-systemd
inherit setuptools

RRECOMMENDS_${PN} += "virtual-obmc-wsgihost"

RDEPENDS_${PN} += " \
        python-xml \
        python-dbus \
        obmc-mapper \
        python-bottle \
        python-spwd \
        pyphosphor-utils \
        pyphosphor-dbus \
        pyphosphor-wsgi-apps-ns \
        "
SRC_URI += "git://github.com/bradbishop/phosphor-rest-server;branch=gevent"

SRCREV = "2c6fc760919cc214413874d60489e3643b639692"

S = "${WORKDIR}/git/module"
SYSTEMD_SERVICE_${PN} = ""
SYSTEMD_OVERRIDE_${PN} += "rest-dbus.conf:obmc-mapper.target.d/rest-dbus.conf"
SYSTEMD_ENVIRONMENT_FILE_${PN} += "obmc/wsgi_app"
