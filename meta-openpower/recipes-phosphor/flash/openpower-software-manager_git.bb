SUMMARY = "OpenPower Software Management"
DESCRIPTION = "OpenPower Software Manager provides a set of host software \
management daemons. It is suitable for use on a wide variety of OpenPower \
platforms."
HOMEPAGE = "https://github.com/openbmc/openpower-pnor-code-mgmt"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit autotools pkgconfig
inherit obmc-phosphor-dbus-service
inherit pythonnative

PACKAGECONFIG[verify_pnor_signature] = "--enable-verify_pnor_signature,--disable-verify_pnor_signature"

EXTRA_OECONF += " \
    PNOR_MSL="v2.0.10 v2.2" \
    "

DEPENDS += " \
        autoconf-archive-native \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        sdbusplus-native \
        "

RDEPENDS_${PN} += " \
        mtd-utils-ubifs \
        phosphor-dbus-interfaces \
        phosphor-logging \
        sdbusplus \
        virtual-obmc-image-manager \
        "

S = "${WORKDIR}/git"

SRC_URI += "git://github.com/openbmc/openpower-pnor-code-mgmt"

SRC_URI += "file://obmc-flash-bios"

SRCREV = "17f55a823134a35b7847b2ae4077ae81f9c0cf79"

do_install_append() {
        install -d ${D}${sbindir}
        install -m 0755 ${WORKDIR}/obmc-flash-bios ${D}${sbindir}/obmc-flash-bios
}

DBUS_SERVICE_${PN} += "org.open_power.Software.Host.Updater.service"

SYSTEMD_SERVICE_${PN} += " \
        obmc-flash-bios-ubiattach.service \
        obmc-flash-bios-ubimount@.service \
        obmc-flash-bios-ubiumount-ro@.service \
        obmc-flash-bios-ubiumount-rw@.service \
        obmc-flash-bios-ubipatch.service \
        obmc-flash-bios-ubiremount.service \
        obmc-flash-bios-updatesymlinks.service \
        obmc-flash-bios-cleanup.service \
        obmc-flash-bios-enable-clearvolatile@.service \
        obmc-flash-bios-check-clearvolatile@.service \
        op-pnor-msl.service \
        "

ENABLE_CLEAR_VOLATILE_TMPL = "obmc-flash-bios-enable-clearvolatile@.service"
HOST_START_TGTFMT = "obmc-host-start@{0}.target"
ENABLE_CLEAR_VOLATILE_INSTFMT = "obmc-flash-bios-enable-clearvolatile@{0}.service"
ENABLE_CLEAR_VOLATILE_START_FMT = "../${ENABLE_CLEAR_VOLATILE_TMPL}:${HOST_START_TGTFMT}.requires/${ENABLE_CLEAR_VOLATILE_INSTFMT}"

CHECK_CLEAR_VOLATILE_TMPL = "obmc-flash-bios-check-clearvolatile@.service"
HOST_STARTMIN_TGTFMT = "obmc-host-startmin@{0}.target"
CHECK_CLEAR_VOLATILE_INSTFMT = "obmc-flash-bios-check-clearvolatile@{0}.service"
CHECK_CLEAR_VOLATILE_START_FMT = "../${CHECK_CLEAR_VOLATILE_TMPL}:${HOST_STARTMIN_TGTFMT}.requires/${CHECK_CLEAR_VOLATILE_INSTFMT}"

SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'ENABLE_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"
SYSTEMD_LINK_${PN} += "${@compose_list_zip(d, 'CHECK_CLEAR_VOLATILE_START_FMT', 'OBMC_HOST_INSTANCES')}"
