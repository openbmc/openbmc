#
# Newlib configuration
#

LIBCEXTENSION = "-newlib"
LIBCOVERRIDE = ":libc-newlib"

PREFERRED_PROVIDER_virtual/libc ?= "newlib"
PREFERRED_PROVIDER_virtual/libiconv ?= "newlib"
PREFERRED_PROVIDER_virtual/libintl ?= "newlib"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"

DISTRO_FEATURES_BACKFILL_CONSIDERED += "ldconfig"

#USE_NLS ?= "no"

IMAGE_LINGUAS = ""

LIBC_DEPENDENCIES = "\
    newlib-dbg \
    newlib-dev \
    libgloss \
    libgloss-dev \
    libgloss-dbg \
    libgcc-dev \
    libgcc-dbg \
    "

# compilerlibs defaults to gcc-runtime, but we get runtime from libgloss
# we set ASSUME_PROVIDED because we cant set PREFERRED_PROVIDER
# for compilerlibs since its overridden by tcmode-default
ASSUME_PROVIDED += "virtual/${TARGET_PREFIX}compilerlibs virtual/crypt"

# Its useful to be able to extend newlib, but we dont provide a native variant of libgloss
NEWLIB_EXTENDED ?=  "libgloss libgcc"
BASE_DEFAULT_DEPS_append_class-target = " ${NEWLIB_EXTENDED}"

TARGET_OS = "elf"
TARGET_OS_arm = "eabi"

TOOLCHAIN_HOST_TASK ?= "packagegroup-cross-canadian-${MACHINE}"
TOOLCHAIN_TARGET_TASK ?= "${LIBC_DEPENDENCIES}"
TOOLCHAIN_NEED_CONFIGSITE_CACHE_remove = "zlib ncurses"

# disable pie security flags by default
SECURITY_CFLAGS_libc-newlib = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS_libc-newlib = ""
