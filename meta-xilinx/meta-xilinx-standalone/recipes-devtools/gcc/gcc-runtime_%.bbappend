require gcc-configure-xilinx-standalone.inc

COMPATIBLE_HOST = "${HOST_SYS}"

EXTRA_OECONF_append_xilinx-standalone_class-target = " \
	--disable-libstdcxx-pch \
	--with-newlib \
	--disable-threads \
	--enable-plugins \
	--with-gnu-as \
	--disable-libitm \
"

EXTRA_OECONF_append_xilinx-standalone_aarch64_class-target = " \
	--disable-multiarch \
	--with-arch=armv8-a \
	"

# Both arm and armrm overrides are set w/ cortex r5
# So only set rmprofile if armrm is defined.
ARM_PROFILE = "aprofile"
ARM_PROFILE_armrm = "rmprofile"

EXTRA_OECONF_append_xilinx-standalone_arm_class-target = " \
	--with-multilib-list=${ARM_PROFILE} \
	"

EXTRA_OECONF_append_xilinx-standalone_armrm_class-target = " \
	--disable-tls \
	--disable-decimal-float \
	"

EXTRA_OECONF_append_xilinx-standalone_microblaze_class-target = " \
	--enable-target-optspace \
	--without-long-double-128 \
	"

# Changes local to gcc-runtime...

# Dont build libitm, etc.
RUNTIMETARGET_xilinx-standalone_class-target = "libstdc++-v3"

do_install_append_xilinx-standalone_class-target() {
	# Fixup what gcc-runtime normally would do, we don't want linux directories!
	rm -rf ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR}-linux

	# The multilibs have different headers, so stop combining them!
	if [ "${TARGET_VENDOR_MULTILIB_ORIGINAL}" != "" -a "${TARGET_VENDOR}" != "${TARGET_VENDOR_MULTILIB_ORIGINAL}" ]; then
		rm -rf ${D}${includedir}/c++/${BINV}/${TARGET_ARCH}${TARGET_VENDOR_MULTILIB_ORIGINAL}-${TARGET_OS}
	fi

	# link the C++ header into the place that multilib gcc expects
	# C++ compiler looks at usr/include/c++/version/canonical-arch/mlib
	if [ "${TARGET_SYS_MULTILIB_ORIGINAL}" != "" -a "${TARGET_SYS_MULTILIB_ORIGINAL}" != "${TARGET_SYS}" ]; then
		mlib=${BASE_LIB_tune-${DEFAULTTUNE}}
                mlib=${mlib##lib/}

		link_name=${D}${includedir}/c++/${BINV}/${TARGET_SYS_MULTILIB_ORIGINAL}/${mlib}
		target=${D}${includedir}/c++/${BINV}/${TARGET_SYS}

		echo mkdir -p $link_name
		mkdir -p $link_name
		for each in bits ext ; do
			relpath=$(python3 -c "import os.path; print(os.path.relpath('$target/$each', '$(dirname $link_name/$each)'))")

			echo ln -s $relpath $link_name/$each
			ln -s $relpath $link_name/$each
		done
	fi
}

FILES_${PN}-dbg_append_xilinx-standalone_class-target = "\
    ${libdir}/libstdc++.a-gdb.py \
"
