#
# Default toolchain configuration
#

PREFERRED_PROVIDER_virtual/cross-binutils = "${MLPREFIX}binutils-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/cross-binutils:class-nativesdk ?= "binutils-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/nativesdk-cross-binutils ?= "binutils-crosssdk-${SDK_SYS}"

# Default libc config
PREFERRED_PROVIDER_virtual/gettext ??= "gettext"

GCCVERSION ?= "15.%"
SDKGCCVERSION ?= "${GCCVERSION}"
GLIBCVERSION ?= "2.41%"
RUSTVERSION ?= "1.87.0%"

PREFERRED_VERSION_gcc ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-cross-${TARGET_ARCH} ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-crosssdk-${SDK_SYS} ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-runtime ?= "${GCCVERSION}"
PREFERRED_VERSION_gcc-sanitizers ?= "${GCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc-runtime ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc-sanitizers ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_libgcc ?= "${GCCVERSION}"
PREFERRED_VERSION_libgcc-initial ?= "${GCCVERSION}"
PREFERRED_VERSION_libgfortran ?= "${GCCVERSION}"
PREFERRED_VERSION_nativesdk-gcc ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-libgcc ?= "${SDKGCCVERSION}"
PREFERRED_VERSION_nativesdk-libgcc-initial ?= "${SDKGCCVERSION}"

PREFERRED_VERSION_glibc                    ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-locale             ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-mtrace             ?= "${GLIBCVERSION}"
PREFERRED_VERSION_glibc-scripts            ?= "${GLIBCVERSION}"
PREFERRED_VERSION_nativesdk-glibc          ?= "${GLIBCVERSION}"
PREFERRED_VERSION_cross-localedef-native   ?= "${GLIBCVERSION}"

# Rust toolchain preferred versions:

PREFERRED_VERSION_cargo ?= "${RUSTVERSION}"
PREFERRED_VERSION_cargo-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_libstd-rs ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-cross-${TARGET_ARCH} ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-native ?= "${RUSTVERSION}"

