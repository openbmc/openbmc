require libgcc-common.inc

DEPENDS = "virtual/${TARGET_PREFIX}gcc-initial"

LICENSE = "GPL-3.0-with-GCC-exception"

STAGINGCC = "gcc-cross-initial-${TARGET_ARCH}"
STAGINGCC_class-nativesdk = "gcc-crosssdk-initial-${SDK_SYS}"
PATH_prepend = "${STAGING_BINDIR_TOOLCHAIN}.${STAGINGCC}:"

PACKAGES = ""

EXTRA_OECONF += "--disable-shared"

COMPILERINITIAL = "-initial"

inherit nopackages

# We really only want this built by things that need it, not any recrdeptask
deltask do_build
