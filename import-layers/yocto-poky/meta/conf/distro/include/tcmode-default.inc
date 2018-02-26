#
# Default toolchain configuration
#

PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}binutils = "binutils-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc-initial = "gcc-cross-initial-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}gcc = "gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}g++ = "gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}compilerlibs = "gcc-runtime"
PREFERRED_PROVIDER_gdb = "gdb"

PREFERRED_PROVIDER_virtual/${SDK_PREFIX}binutils-crosssdk ?= "binutils-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}gcc-initial = "gcc-crosssdk-initial-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}gcc = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}g++ = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/${SDK_PREFIX}compilerlibs = "nativesdk-gcc-runtime"

# Default libc config
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-for-gcc = "${TCLIBC}"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-for-gcc ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/${TARGET_PREFIX}libc-initial = "${TCLIBC}-initial"
PREFERRED_PROVIDER_virtual/nativesdk-${SDK_PREFIX}libc-initial ?= "nativesdk-glibc-initial"
PREFERRED_PROVIDER_virtual/gettext ??= "gettext"

GCCVERSION ?= "7.%"
SDKGCCVERSION ?= "${GCCVERSION}"
BINUVERSION ?= "2.29%"
GDBVERSION ?= "8.0%"
GLIBCVERSION ?= "2.26%"
LINUXLIBCVERSION ?= "4.12%"

PREFERRED_VERSION_gcc ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-cross-${TARGET_ARCH} ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-cross-initial-${TARGET_ARCH} ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-crosssdk-${SDK_SYS} ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_gcc-crosssdk-initial-${SDK_SYS} ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-runtime ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-sanitizers ?= "${GCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc-runtime ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc-sanitizers ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_libgcc ?= "${GCCVERSION}"
PREFERRED_VERSION_libgcc-initial ?= "${GCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-libgcc ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-libgcc-initial ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_binutils ?= "${BINUVERSION}"
PREFERRED_VERSION_binutils-native ?= "${BINUVERSION}"
PREFERRED_VERSION_binutils-cross-${TARGET_ARCH} ?= "${BINUVERSION}"
PREFERRED_VERSION_binutils-crosssdk-${SDK_SYS} ?= "${BINUVERSION}"
PREFERRED_VERSION_binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "${BINUVERSION}"
PREFERRED_VERSION_gdb ?= "${GDBVERSION}"
PREFERRED_VERSION_gdb-cross-${TARGET_ARCH} ?= "${GDBVERSION}"
PREFERRED_VERSION_gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "${GDBVERSION}"

PREFERRED_VERSION_linux-libc-headers ?= "${LINUXLIBCVERSION}"
PREFERRED_VERSION_nativesdk-linux-libc-headers ?= "${LINUXLIBCVERSION}"
PREFERRED_VERSION_glibc                    ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-locale             ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-mtrace             ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-scripts            ?= "${GLIBCVERSION}"
PREFERRED_VERSION_nativesdk-glibc          ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-initial            ?= "${GLIBCVERSION}"
PREFERRED_VERSION_nativesdk-glibc-initial  ?= "${GLIBCVERSION}"
PREFERRED_VERSION_cross-localedef-native   ?= "${GLIBCVERSION}"
# don't use version earlier than 1.4 for gzip-native, as it's necessary for
# some packages using an archive format incompatible with earlier gzip
PREFERRED_VERSION_gzip-native ?= "1.8"

# Setup suitable toolchain flags
require conf/distro/include/as-needed.inc

