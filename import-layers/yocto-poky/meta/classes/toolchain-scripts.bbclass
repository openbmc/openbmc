inherit toolchain-scripts-base siteinfo kernel-arch

# We want to be able to change the value of MULTIMACH_TARGET_SYS, because it
# doesn't always match our expectations... but we default to the stock value
REAL_MULTIMACH_TARGET_SYS ?= "${MULTIMACH_TARGET_SYS}"
TARGET_CC_ARCH_append_libc-uclibc = " -muclibc"
TARGET_CC_ARCH_append_libc-musl = " -mmusl"

# default debug prefix map isn't valid in the SDK
DEBUG_PREFIX_MAP = ""

# This function creates an environment-setup-script for use in a deployable SDK
toolchain_create_sdk_env_script () {
	# Create environment setup script.  Remember that $SDKTARGETSYSROOT should
	# only be expanded on the target at runtime.
	base_sbindir=${10:-${base_sbindir_nativesdk}}
	base_bindir=${9:-${base_bindir_nativesdk}}
	sbindir=${8:-${sbindir_nativesdk}}
	sdkpathnative=${7:-${SDKPATHNATIVE}}
	prefix=${6:-${prefix_nativesdk}}
	bindir=${5:-${bindir_nativesdk}}
	libdir=${4:-${libdir}}
	sysroot=${3:-${SDKTARGETSYSROOT}}
	multimach_target_sys=${2:-${REAL_MULTIMACH_TARGET_SYS}}
	script=${1:-${SDK_OUTPUT}/${SDKPATH}/environment-setup-$multimach_target_sys}
	rm -f $script
	touch $script
	echo 'export SDKTARGETSYSROOT='"$sysroot" >> $script
	EXTRAPATH=""
	for i in ${CANADIANEXTRAOS}; do
		EXTRAPATH="$EXTRAPATH:$sdkpathnative$bindir/${TARGET_ARCH}${TARGET_VENDOR}-$i"
	done
	echo "export PATH=$sdkpathnative$bindir:$sdkpathnative$sbindir:$sdkpathnative$base_bindir:$sdkpathnative$base_sbindir:$sdkpathnative$bindir/../${HOST_SYS}/bin:$sdkpathnative$bindir/${TARGET_SYS}"$EXTRAPATH':$PATH' >> $script
	echo "export CCACHE_PATH=$sdkpathnative$bindir:$sdkpathnative$bindir/../${HOST_SYS}/bin:$sdkpathnative$bindir/${TARGET_SYS}"$EXTRAPATH':$CCACHE_PATH' >> $script
	echo 'export PKG_CONFIG_SYSROOT_DIR=$SDKTARGETSYSROOT' >> $script
	echo 'export PKG_CONFIG_PATH=$SDKTARGETSYSROOT'"$libdir"'/pkgconfig:$SDKTARGETSYSROOT'"$prefix"'/share/pkgconfig' >> $script
	echo 'export CONFIG_SITE=${SDKPATH}/site-config-'"${multimach_target_sys}" >> $script
	echo "export OECORE_NATIVE_SYSROOT=\"$sdkpathnative\"" >> $script
	echo 'export OECORE_TARGET_SYSROOT="$SDKTARGETSYSROOT"' >> $script
	echo "export OECORE_ACLOCAL_OPTS=\"-I $sdkpathnative/usr/share/aclocal\"" >> $script
	echo 'unset command_not_found_handle' >> $script

	toolchain_shared_env_script
}

# This function creates an environment-setup-script in the TMPDIR which enables
# a OE-core IDE to integrate with the build tree
toolchain_create_tree_env_script () {
	script=${TMPDIR}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
	rm -f $script
	touch $script
	echo 'export PATH=${STAGING_DIR_NATIVE}/usr/bin:${PATH}' >> $script
	echo 'export CCACHE_PATH=${STAGING_DIR_NATIVE}/usr/bin:${CCACHE_PATH}' >> $script
	echo 'export PKG_CONFIG_SYSROOT_DIR=${PKG_CONFIG_SYSROOT_DIR}' >> $script
	echo 'export PKG_CONFIG_PATH=${PKG_CONFIG_PATH}' >> $script
	echo 'export CONFIG_SITE="${@siteinfo_get_files(d)}"' >> $script
	echo 'export SDKTARGETSYSROOT=${STAGING_DIR_TARGET}' >> $script
	echo 'export OECORE_NATIVE_SYSROOT="${STAGING_DIR_NATIVE}"' >> $script
	echo 'export OECORE_TARGET_SYSROOT="${STAGING_DIR_TARGET}"' >> $script
	echo 'export OECORE_ACLOCAL_OPTS="-I ${STAGING_DIR_NATIVE}/usr/share/aclocal"' >> $script

	toolchain_shared_env_script
}

toolchain_shared_env_script () {
	echo 'export CC="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export CXX="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export CPP="${TARGET_PREFIX}gcc -E ${TARGET_CC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export AS="${TARGET_PREFIX}as ${TARGET_AS_ARCH}"' >> $script
	echo 'export LD="${TARGET_PREFIX}ld ${TARGET_LD_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export GDB=${TARGET_PREFIX}gdb' >> $script
	echo 'export STRIP=${TARGET_PREFIX}strip' >> $script
	echo 'export RANLIB=${TARGET_PREFIX}ranlib' >> $script
	echo 'export OBJCOPY=${TARGET_PREFIX}objcopy' >> $script
	echo 'export OBJDUMP=${TARGET_PREFIX}objdump' >> $script
	echo 'export AR=${TARGET_PREFIX}ar' >> $script
	echo 'export NM=${TARGET_PREFIX}nm' >> $script
	echo 'export M4=m4' >> $script
	echo 'export TARGET_PREFIX=${TARGET_PREFIX}' >> $script
	echo 'export CONFIGURE_FLAGS="--target=${TARGET_SYS} --host=${TARGET_SYS} --build=${SDK_ARCH}-linux --with-libtool-sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export CFLAGS="${TARGET_CFLAGS}"' >> $script
	echo 'export CXXFLAGS="${TARGET_CXXFLAGS}"' >> $script
	echo 'export LDFLAGS="${TARGET_LDFLAGS}"' >> $script
	echo 'export CPPFLAGS="${TARGET_CPPFLAGS}"' >> $script
	echo 'export KCFLAGS="--sysroot=$SDKTARGETSYSROOT"' >> $script
	echo 'export OECORE_DISTRO_VERSION="${DISTRO_VERSION}"' >> $script
	echo 'export OECORE_SDK_VERSION="${SDK_VERSION}"' >> $script
	echo 'export ARCH=${ARCH}' >> $script
	echo 'export CROSS_COMPILE=${TARGET_PREFIX}' >> $script

    cat >> $script <<EOF

# Append environment subscripts
if [ -d "\$OECORE_TARGET_SYSROOT/environment-setup.d" ]; then
    for envfile in \$OECORE_TARGET_SYSROOT/environment-setup.d/*.sh; do
	    . \$envfile
    done
fi
if [ -d "\$OECORE_NATIVE_SYSROOT/environment-setup.d" ]; then
    for envfile in \$OECORE_NATIVE_SYSROOT/environment-setup.d/*.sh; do
	    . \$envfile
    done
fi
EOF
}

#we get the cached site config in the runtime
TOOLCHAIN_CONFIGSITE_NOCACHE = "${@siteinfo_get_files(d)}"
TOOLCHAIN_CONFIGSITE_SYSROOTCACHE = "${STAGING_DIR}/${MLPREFIX}${MACHINE}/${target_datadir}/${TARGET_SYS}_config_site.d"
TOOLCHAIN_NEED_CONFIGSITE_CACHE ??= "virtual/${MLPREFIX}libc ncurses"

#This function create a site config file
toolchain_create_sdk_siteconfig () {
	local siteconfig=$1

	rm -f $siteconfig
	touch $siteconfig

	for sitefile in ${TOOLCHAIN_CONFIGSITE_NOCACHE} ; do
		cat $sitefile >> $siteconfig
	done

	#get cached site config
	for sitefile in ${TOOLCHAIN_NEED_CONFIGSITE_CACHE}; do
		# Resolve virtual/* names to the real recipe name using sysroot-providers info
		case $sitefile in virtual/*)
			sitefile=`echo $sitefile | tr / _`
			sitefile=`cat ${STAGING_DIR_TARGET}/sysroot-providers/$sitefile`
		esac

		if [ -r ${TOOLCHAIN_CONFIGSITE_SYSROOTCACHE}/${sitefile}_config ]; then
			cat ${TOOLCHAIN_CONFIGSITE_SYSROOTCACHE}/${sitefile}_config >> $siteconfig
		fi
	done
}
# The immediate expansion above can result in unwanted path dependencies here
toolchain_create_sdk_siteconfig[vardepsexclude] = "TOOLCHAIN_CONFIGSITE_SYSROOTCACHE"

python __anonymous () {
    import oe.classextend
    deps = ""
    for dep in (d.getVar('TOOLCHAIN_NEED_CONFIGSITE_CACHE', True) or "").split():
        deps += " %s:do_populate_sysroot" % dep
        for variant in (d.getVar('MULTILIB_VARIANTS', True) or "").split():
            clsextend = oe.classextend.ClassExtender(variant, d)
            newdep = clsextend.extend_name(dep)
            deps += " %s:do_populate_sysroot" % newdep
    d.appendVarFlag('do_configure', 'depends', deps)
}
