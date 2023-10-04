SUMMARY = "Provider of the machine specific securetty file"
SECTION = "base utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

INHIBIT_DEFAULT_DEPS = "1"


SRC_URI = "file://securetty"

S = "${WORKDIR}"

# Since SERIAL_CONSOLES is likely to be set from the machine configuration
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install () {
	# Ensure we add a suitable securetty file to the package that has
	# most common embedded TTYs defined.
	install -d ${D}${sysconfdir}
	install -m 0400 ${WORKDIR}/securetty ${D}${sysconfdir}/securetty
	if [ ! -z "${SERIAL_CONSOLES}" ]; then
		# Our SERIAL_CONSOLES contains a baud rate and sometimes extra
		# options as well. The following pearl :) takes that and converts
		# it into newline-separated tty's and appends them into
		# securetty. So if a machine has a weird looking console device
		# node (e.g. ttyAMA0) that securetty does not know, it will get
		# appended to securetty and root logins will be allowed on that
		# console.
		tmp="${SERIAL_CONSOLES}"
		for entry in $tmp ; do
			ttydev=`echo "$entry" | sed -e 's/^[0-9]*\;//' -e 's/\;.*//'`
			if ! grep -q $ttydev ${D}${sysconfdir}/securetty; then
				echo $ttydev >> ${D}${sysconfdir}/securetty
			fi
		done
	fi
}
