SUMMARY = "inittab configuration for BusyBox"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://inittab"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

INHIBIT_DEFAULT_DEPS = "1"

do_compile() {
	:
}

do_install() {
	install -d ${D}${sysconfdir}
	install -D -m 0644 ${S}/inittab ${D}${sysconfdir}/inittab

    CONSOLES="${SERIAL_CONSOLES}"
    for s in $CONSOLES
    do
        speed=$(echo $s | cut -d\; -f 1)
        device=$(echo $s | cut -d\; -f 2)
        label=$(echo $device | sed -e 's/tty//' | tail --bytes=5)

        echo "$device::respawn:${sbindir}/ttyrun $device ${base_sbindir}/getty $speed $device" >> ${D}${sysconfdir}/inittab
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
			echo "tty$n:12345:respawn:${base_sbindir}/getty 38400 tty$n" >> ${D}${sysconfdir}/inittab
		done
		echo "" >> ${D}${sysconfdir}/inittab
	fi

}


# SERIAL_CONSOLES is generally defined by the MACHINE .conf.
# Set PACKAGE_ARCH appropriately.
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} = "${sysconfdir}/inittab"
CONFFILES:${PN} = "${sysconfdir}/inittab"

RDEPENDS:${PN} = "ttyrun"
RCONFLICTS:${PN} = "sysvinit-inittab"

USE_VT ?= "1"
SYSVINIT_ENABLED_GETTYS ?= "1"
