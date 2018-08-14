SUMMARY = "QEMU wrapper script"
HOMEPAGE = "http://qemu.org"
LICENSE = "MIT"

S = "${WORKDIR}"

DEPENDS += "qemu-native"

inherit qemu

do_populate_sysroot[depends] = ""

do_install () {
	install -d ${D}${bindir_crossscripts}/

	echo "#!/bin/sh" > ${D}${bindir_crossscripts}/qemuwrapper
	qemu_binary=${@qemu_target_binary(d)}
	qemu_options='${QEMU_OPTIONS}'
	echo "$qemu_binary $qemu_options \"\$@\"" >> ${D}${bindir_crossscripts}/qemuwrapper
	fallback_qemu_bin=
	case $qemu_binary in
		"qemu-i386")
			fallback_qemu_bin=qemu-x86_64
			;;
		"qemu-x86_64")
			fallback_qemu_bin=qemu-i386
			;;
		*)
			;;
	esac

	if [ -n "$fallback_qemu_bin" ]; then

		cat >> ${D}${bindir_crossscripts}/qemuwrapper << EOF
rc=\$?
if [ \$rc = 255 ]; then
	$fallback_qemu_bin "\$@"
	rc=\$?
fi
exit \$rc
EOF

	fi

	chmod +x ${D}${bindir_crossscripts}/qemuwrapper
}

SYSROOT_DIRS += "${bindir_crossscripts}"

INHIBIT_DEFAULT_DEPS = "1"
