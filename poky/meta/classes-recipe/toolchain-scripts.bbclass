#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit toolchain-scripts-base siteinfo kernel-arch

# We want to be able to change the value of MULTIMACH_TARGET_SYS, because it
# doesn't always match our expectations... but we default to the stock value
REAL_MULTIMACH_TARGET_SYS ?= "${MULTIMACH_TARGET_SYS}"
TARGET_CC_ARCH:append:libc-musl = " -mmusl"

# default debug prefix map isn't valid in the SDK
DEBUG_PREFIX_MAP = ""

EXPORT_SDK_PS1 = "${@ 'export PS1=\\"%s\\"' % d.getVar('SDK_PS1') if d.getVar('SDK_PS1') else ''}"

def siteinfo_with_prefix(d, prefix):
    # Return a prefixed value from siteinfo
    for item in siteinfo_data_for_machine(d.getVar("TARGET_ARCH"), d.getVar("TARGET_OS"), d):
        if item.startswith(prefix):
            return item.replace(prefix, "")
    raise KeyError

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

	echo '# Check for LD_LIBRARY_PATH being set, which can break SDK and generally is a bad practice' >> $script
	echo '# http://tldp.org/HOWTO/Program-Library-HOWTO/shared-libraries.html#AEN80' >> $script
	echo '# http://xahlee.info/UnixResource_dir/_/ldpath.html' >> $script
	echo '# Only disable this check if you are absolutely know what you are doing!' >> $script
	echo 'if [ ! -z "${LD_LIBRARY_PATH:-}" ]; then' >> $script
	echo "    echo \"Your environment is misconfigured, you probably need to 'unset LD_LIBRARY_PATH'\"" >> $script
	echo "    echo \"but please check why this was set in the first place and that it's safe to unset.\"" >> $script
	echo '    echo "The SDK will not operate correctly in most cases when LD_LIBRARY_PATH is set."' >> $script
	echo '    echo "For more references see:"' >> $script
	echo '    echo "  http://tldp.org/HOWTO/Program-Library-HOWTO/shared-libraries.html#AEN80"' >> $script
	echo '    echo "  http://xahlee.info/UnixResource_dir/_/ldpath.html"' >> $script
	echo '    return 1' >> $script
	echo 'fi' >> $script

	echo "${EXPORT_SDK_PS1}" >> $script
	echo 'export SDKTARGETSYSROOT='"$sysroot" >> $script
	EXTRAPATH=""
	for i in ${CANADIANEXTRAOS}; do
		EXTRAPATH="$EXTRAPATH:$sdkpathnative$bindir/${TARGET_ARCH}${TARGET_VENDOR}-$i"
	done
	echo "export PATH=$sdkpathnative$bindir:$sdkpathnative$sbindir:$sdkpathnative$base_bindir:$sdkpathnative$base_sbindir:$sdkpathnative$bindir/../${HOST_SYS}/bin:$sdkpathnative$bindir/${TARGET_SYS}"$EXTRAPATH':"$PATH"' >> $script
	echo 'export PKG_CONFIG_SYSROOT_DIR=$SDKTARGETSYSROOT' >> $script
	echo 'export PKG_CONFIG_PATH=$SDKTARGETSYSROOT'"$libdir"'/pkgconfig:$SDKTARGETSYSROOT'"$prefix"'/share/pkgconfig' >> $script
	echo 'export CONFIG_SITE=${SDKPATH}/site-config-'"${multimach_target_sys}" >> $script
	echo "export OECORE_NATIVE_SYSROOT=\"$sdkpathnative\"" >> $script
	echo 'export OECORE_TARGET_SYSROOT="$SDKTARGETSYSROOT"' >> $script
	echo "export OECORE_ACLOCAL_OPTS=\"-I $sdkpathnative/usr/share/aclocal\"" >> $script
	echo 'export OECORE_BASELIB="${baselib}"' >> $script
	echo 'export OECORE_TARGET_ARCH="${TARGET_ARCH}"' >>$script
	echo 'export OECORE_TARGET_OS="${TARGET_OS}"' >>$script
	echo 'export OECORE_TARGET_BITS="${@siteinfo_with_prefix(d, 'bit-')}"' >>$script
	echo 'export OECORE_TARGET_ENDIAN="${@siteinfo_with_prefix(d, 'endian-')}"' >>$script

	echo 'unset command_not_found_handle' >> $script

	toolchain_shared_env_script
}

# This function creates an environment-setup-script in B which enables
# a OE-core IDE to integrate with the build tree
# Caller must ensure CONFIG_SITE is setup
toolchain_create_tree_env_script () {
	script=${B}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
	rm -f $script
	touch $script
	echo 'standalone_sysroot_target="${STAGING_DIR}/${MACHINE}"' >> $script
	echo 'standalone_sysroot_native="${STAGING_DIR}/${BUILD_ARCH}"' >> $script
	echo 'orig=`pwd`; cd ${COREBASE}; . ./oe-init-build-env ${TOPDIR}; cd $orig' >> $script
	echo 'export PATH=$standalone_sysroot_native/${bindir_native}:$standalone_sysroot_native/${bindir_native}/${TARGET_SYS}:$PATH' >> $script
	echo 'export PKG_CONFIG_SYSROOT_DIR=$standalone_sysroot_target' >> $script
	echo 'export PKG_CONFIG_PATH=$standalone_sysroot_target'"$libdir"'/pkgconfig:$standalone_sysroot_target'"$prefix"'/share/pkgconfig' >> $script
	echo 'export CONFIG_SITE="${CONFIG_SITE}"' >> $script
	echo 'export SDKTARGETSYSROOT=$standalone_sysroot_target' >> $script
	echo 'export OECORE_NATIVE_SYSROOT=$standalone_sysroot_native' >> $script
	echo 'export OECORE_TARGET_SYSROOT=$standalone_sysroot_target' >> $script
	echo 'export OECORE_ACLOCAL_OPTS="-I $standalone_sysroot_native/usr/share/aclocal"' >> $script
	echo 'export OECORE_BASELIB="${baselib}"' >> $script
	echo 'export OECORE_TARGET_ARCH="${TARGET_ARCH}"' >>$script
	echo 'export OECORE_TARGET_OS="${TARGET_OS}"' >>$script

	toolchain_shared_env_script

	cat >> $script <<EOF

if [ -d "\$OECORE_NATIVE_SYSROOT/${datadir}/post-relocate-setup.d/" ]; then
	for s in \$OECORE_NATIVE_SYSROOT/${datadir}/post-relocate-setup.d/*; do
		if [ ! -x \$s ]; then
			continue
		fi
		\$s "\$1"
		status=\$?
		if [ \$status != 0 ]; then
			echo "post-relocate command \"\$s \$1\" failed with status \$status" >&2
			exit \$status
		fi
	done
fi
EOF
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
	echo 'export READELF=${TARGET_PREFIX}readelf' >> $script
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
	echo 'export OECORE_TUNE_CCARGS="${TUNE_CCARGS}"' >> $script

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

toolchain_create_post_relocate_script() {
	relocate_script=$1
	env_dir=$2
	rm -f $relocate_script
	touch $relocate_script

	cat >> $relocate_script <<EOF
if [ -d "${SDKPATHNATIVE}/post-relocate-setup.d/" ]; then
	# Source top-level SDK env scripts in case they are needed for the relocate
	# scripts.
	for env_setup_script in ${env_dir}/environment-setup-*; do
		. \$env_setup_script
		status=\$?
		if [ \$status != 0 ]; then
			echo "\$0: Failed to source \$env_setup_script with status \$status"
			exit \$status
		fi

		for s in ${SDKPATHNATIVE}/post-relocate-setup.d/*; do
			if [ ! -x \$s ]; then
				continue
			fi
			\$s "\$1"
			status=\$?
			if [ \$status != 0 ]; then
				echo "post-relocate command \"\$s \$1\" failed with status \$status" >&2
				exit \$status
			fi
		done
	done
	rm -rf "${SDKPATHNATIVE}/post-relocate-setup.d"
fi
EOF
}

#we get the cached site config in the runtime
TOOLCHAIN_CONFIGSITE_NOCACHE = "${@' '.join(siteinfo_get_files(d)[0])}"
TOOLCHAIN_NEED_CONFIGSITE_CACHE ??= "virtual/${MLPREFIX}libc ncurses"
DEPENDS += "${TOOLCHAIN_NEED_CONFIGSITE_CACHE}"

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
	done
}

python __anonymous () {
    import oe.classextend
    deps = ""
    for dep in (d.getVar('TOOLCHAIN_NEED_CONFIGSITE_CACHE') or "").split():
        deps += " %s:do_populate_sysroot" % dep
        for variant in (d.getVar('MULTILIB_VARIANTS') or "").split():
            clsextend = oe.classextend.ClassExtender(variant, d)
            newdep = clsextend.extend_name(dep)
            deps += " %s:do_populate_sysroot" % newdep
    d.appendVarFlag('do_configure', 'depends', deps)
}
