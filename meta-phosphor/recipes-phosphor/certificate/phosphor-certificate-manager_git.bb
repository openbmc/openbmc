SUMMARY = "Phosphor Certificate Manager"
DESCRIPTION = "Manages client and server certificates"
HOMEPAGE = "https://github.com/openbmc/phosphor-certificate-manager"

PR = "r1"
PV = "0.1+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/openbmc/phosphor-certificate-manager"
SRCREV = "811a29e1941db0157f49d2e05491be945f7b2f07"

inherit autotools \
        pkgconfig \
        obmc-phosphor-systemd

DEPENDS = " \
        autoconf-archive-native \
        openssl \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        sdeventplus \
        "

S = "${WORKDIR}/git"

EXTRA_OECONF += "--disable-tests"

CERT_TMPL = "phosphor-certificate-manager@.service"
SYSTEMD_SERVICE_${PN} = "${CERT_TMPL}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[ibm-hypervisor-cert] = "--enable-ca-cert-extension,,"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('PACKAGECONFIG', 'ibm-hypervisor-cert', 'bmc-vmi-ca-manager.service', '', d)}"
