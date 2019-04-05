SUMMARY = "QEMU wrapper script"
HOMEPAGE = "http://qemu.org"
LICENSE = "MIT"

S = "${WORKDIR}"

DEPENDS += "qemu-native"

inherit qemu

do_populate_sysroot[depends] = ""

do_install () {
	install -d ${D}${bindir_crossscripts}/

	qemu_binary=${@qemu_target_binary(d)}
	qemu_options='${QEMU_OPTIONS} -E LD_LIBRARY_PATH=$D${libdir}:$D${base_libdir}'

	cat >> ${D}${bindir_crossscripts}/${MLPREFIX}qemuwrapper << EOF
#!/bin/sh
set -x

if [ ${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d)} = False -a "${PN}" != "nativesdk-qemuwrapper-cross" ]; then
	echo "qemuwrapper: qemu usermode is not supported"
	exit 1
fi


$qemu_binary $qemu_options "\$@"
EOF

	chmod +x ${D}${bindir_crossscripts}/${MLPREFIX}qemuwrapper
}

SYSROOT_DIRS += "${bindir_crossscripts}"

INHIBIT_DEFAULT_DEPS = "1"

BBCLASSEXTEND = "nativesdk"
