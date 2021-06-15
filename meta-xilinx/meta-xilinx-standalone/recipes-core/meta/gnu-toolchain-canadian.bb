inherit cross-canadian

SUMMARY = "Links to the various GNU toolchains for use with the Xilinx multilib toolchain"
PN = "gnu-toolchain-canadian-${TARGET_SYS}"
BPN = "gnu-toolchain-canadian"

LICENSE = "MIT"

do_install () {
	if [ "${TARGET_SYS_MULTILIB_ORIGINAL}" != "" -a "${TARGET_SYS_MULTILIB_ORIGINAL}" != "${TARGET_SYS}" ]; then
		mkdir -p ${D}${bindir}

		# Create a link for each item references by the environment files
		for each in gcc g++ as ld gdb strip ranlib objcopy objdump readelf ar nm ; do
			ln -s ../${TARGET_SYS_MULTILIB_ORIGINAL}/${TARGET_SYS_MULTILIB_ORIGINAL}-${each} ${D}${bindir}/${TARGET_SYS}-$each
		done
	fi
}

INHIBIT_DEFAULT_DEPS = "1"

ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = "${bindir}"

PACKAGES = "${PN}"
