DESCRIPTION = "SDK type target for building a standalone tarball containing python3, chrpath, make, git and tar. The \
               tarball can be used to run bitbake builds on systems which don't meet the usual version requirements."
SUMMARY = "Standalone tarball for running builds on systems with inadequate software"
LICENSE = "MIT"

TOOLCHAIN_TARGET_TASK ?= ""

TOOLCHAIN_HOST_TASK ?= "\
    nativesdk-python3-core \
    nativesdk-python3-modules \
    nativesdk-python3-misc \
    nativesdk-python3-git \
    nativesdk-python3-testtools \
    nativesdk-python3-subunit \
    nativesdk-ncurses-terminfo-base \
    nativesdk-chrpath \
    nativesdk-tar \
    nativesdk-buildtools-perl-dummy \
    nativesdk-git \
    nativesdk-git-perltools \
    nativesdk-pigz \
    nativesdk-make \
    nativesdk-wget \
    nativesdk-ca-certificates \
    nativesdk-texinfo \
    nativesdk-libnss-nis \
    nativesdk-rpcsvc-proto \
    "

MULTIMACH_TARGET_SYS = "${SDK_ARCH}-nativesdk${SDK_VENDOR}-${SDK_OS}"
PACKAGE_ARCH = "${SDK_ARCH}_${SDK_OS}"
PACKAGE_ARCHS = ""
TARGET_ARCH = "none"
TARGET_OS = "none"

SDK_PACKAGE_ARCHS += "buildtools-dummy-${SDKPKGSUFFIX}"

TOOLCHAIN_OUTPUTNAME ?= "${SDK_ARCH}-buildtools-nativesdk-standalone-${DISTRO_VERSION}"

SDK_TITLE = "Build tools"

RDEPENDS = "${TOOLCHAIN_HOST_TASK}"

EXCLUDE_FROM_WORLD = "1"

inherit meta
inherit populate_sdk
inherit toolchain-scripts-base
inherit nopackages

deltask install
deltask populate_sysroot

do_populate_sdk[stamp-extra-info] = "${PACKAGE_ARCH}"

REAL_MULTIMACH_TARGET_SYS = "none"

create_sdk_files_append () {
	rm -f ${SDK_OUTPUT}/${SDKPATH}/site-config-*
	rm -f ${SDK_OUTPUT}/${SDKPATH}/environment-setup-*
	rm -f ${SDK_OUTPUT}/${SDKPATH}/version-*

	# Generate new (mini) sdk-environment-setup file
	script=${1:-${SDK_OUTPUT}/${SDKPATH}/environment-setup-${SDK_SYS}}
	touch $script
	echo 'export PATH=${SDKPATHNATIVE}${bindir_nativesdk}:$PATH' >> $script
	# In order for the self-extraction script to correctly extract and set up things,
	# we need a 'OECORE_NATIVE_SYSROOT=xxx' line in environment setup script.
	# However, buildtools-tarball is inherently a tool set instead of a fully functional SDK,
	# so instead of exporting the variable, we use a comment here.
	echo '#OECORE_NATIVE_SYSROOT="${SDKPATHNATIVE}"' >> $script
	toolchain_create_sdk_version ${SDK_OUTPUT}/${SDKPATH}/version-${SDK_SYS}

	echo 'export GIT_SSL_CAINFO="${SDKPATHNATIVE}${sysconfdir}/ssl/certs/ca-certificates.crt"' >>$script

	if [ "${SDKMACHINE}" = "i686" ]; then
		echo 'export NO32LIBS="0"' >>$script
		echo 'echo "$BB_ENV_EXTRAWHITE" | grep -q "NO32LIBS"' >>$script
		echo '[ $? != 0 ] && export BB_ENV_EXTRAWHITE="NO32LIBS $BB_ENV_EXTRAWHITE"' >>$script
	fi
}

# buildtools-tarball doesn't need config site
TOOLCHAIN_NEED_CONFIGSITE_CACHE = ""

# The recipe doesn't need any default deps
INHIBIT_DEFAULT_DEPS = "1"
