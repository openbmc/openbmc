SUMMARY = "Wrapper script for the Linux kernel module dependency indexer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"
# The kernel and the staging dir for it is machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# We need the following for the sstate code to process the wrapper
SSTATE_SCAN_FILES += "depmodwrapper"

do_install() {
	install -d ${D}${bindir_crossscripts}/

	cat > ${D}${bindir_crossscripts}/depmodwrapper << EOF
#!/bin/sh
# Expected to be called as: depmodwrapper -a KERNEL_VERSION
if [ "\$1" != "-a" -o "\$2" != "-b" ]; then
    echo "Usage: depmodwrapper -a -b rootfs KERNEL_VERSION" >&2
    exit 1
fi
if [ ! -r ${PKGDATA_DIR}/kernel-depmod/kernel-abiversion ]; then
    echo "Unable to read: ${PKGDATA_DIR}/kernel-depmod/kernel-abiversion" >&2
else
    kernelabi=\$(cat ${PKGDATA_DIR}/kernel-depmod/kernel-abiversion)
    if [ "\$kernelabi" != "\$4" ]; then
        echo "Error: Kernel version \$4 does not match kernel-abiversion (\$kernelabi)" >&2
        exit 1
    fi
fi

if [ ! -r ${PKGDATA_DIR}/kernel-depmod/System.map-\$4 ]; then
    echo "Unable to read: ${PKGDATA_DIR}/kernel-depmod/System.map-\$4" >&2
    exec env depmod "\$1" "\$2" "\$3" "\$4"
else
    exec env depmod "\$1" "\$2" "\$3" -F "${PKGDATA_DIR}/kernel-depmod/System.map-\$4" "\$4"
fi
EOF
	chmod +x ${D}${bindir_crossscripts}/depmodwrapper
}

SYSROOT_PREPROCESS_FUNCS += "depmodwrapper_sysroot_preprocess"

depmodwrapper_sysroot_preprocess () {
	sysroot_stage_dir ${D}${bindir_crossscripts} ${SYSROOT_DESTDIR}${bindir_crossscripts}
}

inherit nopackages
