require libgcc-common.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc-initial"

LICENSE = "GPL-3.0-with-GCC-exception"

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${TARGET_ARCH}"
PATH_prepend = "${STAGING_BINDIR_TOOLCHAIN}.${STAGINGCC}:"

PACKAGES = ""

EXTRA_OECONF += "--disable-shared"

LIBGCCBUILDTREENAME = "gcc-build-internal-initial-"

do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_TCBOOTSTRAP}/"

inherit nopackages
