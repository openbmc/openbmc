SUMMARY = "Wrapper script for the Linux kernel module dependency indexer"
LICENSE = "MIT"

S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"
# The kernel and the staging dir for it is machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# We need the following for the sstate code to process the wrapper
SSTATE_SCAN_FILES += "depmodwrapper"
EXTRA_STAGING_FIXMES += "PKGDATA_DIR"

DEPENDS += "kmod-native"
do_populate_sysroot[depends] = ""

do_install() {
	install -d ${D}${bindir_crossscripts}/

	cat > ${D}${bindir_crossscripts}/depmodwrapper << EOF
#!/bin/sh
# Expected to be called as: depmodwrapper -a KERNEL_VERSION
if [ "\$1" != "-a" -o "\$2" != "-b" ]; then
    echo "Usage: depmodwrapper -a -b rootfs KERNEL_VERSION" >&2
    exit 1
fi

kernelabi=""
if [ -r "${PKGDATA_DIR}/kernel-depmod/kernel-abiversion" ]; then
    kernelabi=\$(cat "${PKGDATA_DIR}/kernel-depmod/kernel-abiversion")
fi

if [ ! -r ${PKGDATA_DIR}/kernel-depmod/System.map-\$4 ] || [ "\$kernelabi" != "\$4" ]; then
    echo "Unable to read: ${PKGDATA_DIR}/kernel-depmod/System.map-\$4" >&2
    exec env depmod "\$1" "\$2" "\$3" "\$4"
else
    exec env depmod "\$1" "\$2" "\$3" -F "${PKGDATA_DIR}/kernel-depmod/System.map-\$4" "\$4"
fi
EOF
	chmod +x ${D}${bindir_crossscripts}/depmodwrapper
}

SYSROOT_DIRS += "${bindir_crossscripts}"

PACKAGES = ""
inherit nopackages
