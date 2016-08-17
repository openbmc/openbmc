SUMMARY = "Inittab configuration for SysVinit"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

PR = "r10"

SRC_URI = "file://inittab \
           file://start_getty"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

do_compile() {
	:
}

do_install() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/inittab ${D}${sysconfdir}/inittab
    install -d ${D}${base_bindir}
    install -m 0755 ${WORKDIR}/start_getty ${D}${base_bindir}/start_getty

    set -x
    tmp="${SERIAL_CONSOLES}"
    for i in $tmp
    do
	j=`echo ${i} | sed s/\;/\ /g`
	label=`echo ${i} | sed -e 's/tty//' -e 's/^.*;//' -e 's/;.*//'`
	echo "$label:12345:respawn:${base_bindir}/start_getty ${j}" >> ${D}${sysconfdir}/inittab
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

pkg_postinst_${PN} () {
# run this on the target
if [ "x$D" = "x" ] && [ -e /proc/consoles ]; then
	tmp="${SERIAL_CONSOLES_CHECK}"
	for i in $tmp
	do
		j=`echo ${i} | sed s/^.*\;//g`
		if [ -z "`grep ${j} /proc/consoles`" ]; then
			sed -i /^.*${j}$/d /etc/inittab
		fi
	done
	kill -HUP 1
else
	if [ "${SERIAL_CONSOLES_CHECK}" = "" ]; then
		exit 0
	else
		exit 1
	fi
fi
}

# USE_VT and SERIAL_CONSOLES are generally defined by the MACHINE .conf.
# Set PACKAGE_ARCH appropriately.
PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES_${PN} = "${sysconfdir}/inittab ${base_bindir}/start_getty"
CONFFILES_${PN} = "${sysconfdir}/inittab"

USE_VT ?= "1"
SYSVINIT_ENABLED_GETTYS ?= "1"



