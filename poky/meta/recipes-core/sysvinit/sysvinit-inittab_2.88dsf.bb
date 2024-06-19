SUMMARY = "Inittab configuration for SysVinit"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"


SRC_URI = "file://inittab \
           file://start_getty"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

INHIBIT_DEFAULT_DEPS = "1"

do_compile() {
	:
}

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${S}/inittab ${D}${sysconfdir}/inittab
    install -d ${D}${base_bindir}
    install -m 0755 ${S}/start_getty ${D}${base_bindir}/start_getty
    sed -e 's,/usr/bin,${bindir},g' -i ${D}${base_bindir}/start_getty

    CONSOLES="${SERIAL_CONSOLES}"
    for s in $CONSOLES
    do
        speed=$(echo $s | cut -d\; -f 1)
        device=$(echo $s | cut -d\; -f 2)
        label=$(echo $device | sed -e 's/tty//' | tail --bytes=5)

        echo "$label:12345:respawn:${sbindir}/ttyrun $device ${base_bindir}/start_getty $speed $device vt102" >> ${D}${sysconfdir}/inittab
    done

    if [ "${USE_VT}" = "1" ]; then
        cat <<EOF >>${D}${sysconfdir}/inittab
# ${base_sbindir}/getty invocations for the runlevels.
#
# The "id" field MUST be the same as the last
# characters of the device (after "tty").
#
# Format:
#  <id>:<runlevels>:<action>:<process>
#

EOF

        for n in ${SYSVINIT_ENABLED_GETTYS}
        do
            echo "$n:12345:respawn:${base_sbindir}/getty 38400 tty$n" >> ${D}${sysconfdir}/inittab
        done
        echo "" >> ${D}${sysconfdir}/inittab
    fi
}

# USE_VT and SERIAL_CONSOLES are generally defined by the MACHINE .conf.
# Set PACKAGE_ARCH appropriately.
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} = "${sysconfdir}/inittab ${base_bindir}/start_getty"
CONFFILES:${PN} = "${sysconfdir}/inittab"

USE_VT ?= "1"
SYSVINIT_ENABLED_GETTYS ?= "1"

RDEPENDS:${PN} = "ttyrun"
RCONFLICTS:${PN} = "busybox-inittab"
