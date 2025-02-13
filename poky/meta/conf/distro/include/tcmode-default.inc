#
# Default toolchain configuration
#

PREFERRED_PROVIDER_virtual/cross-binutils = "${MLPREFIX}binutils-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/cross-cc = "${MLPREFIX}gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/cross-c++ = "${MLPREFIX}gcc-cross-${TARGET_ARCH}"
PREFERRED_PROVIDER_virtual/compilerlibs = "gcc-runtime"
PREFERRED_PROVIDER_gdb = "gdb"

PREFERRED_PROVIDER_virtual/cross-binutils:class-nativesdk ?= "binutils-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/cross-cc:class-nativesdk = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/cross-c++:class-nativesdk = "gcc-crosssdk-${SDK_SYS}"

PREFERRED_PROVIDER_virtual/nativesdk-cross-binutils ?= "binutils-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/nativesdk-cross-cc = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/nativesdk-cross-c++ = "gcc-crosssdk-${SDK_SYS}"
PREFERRED_PROVIDER_virtual/nativesdk-compilerlibs = "nativesdk-gcc-runtime"

# Default libc config
PREFERRED_PROVIDER_virtual/gettext ??= "gettext"

GCCVERSION ?= "14.%"
SDKGCCVERSION ?= "${GCCVERSION}"
BINUVERSION ?= "2.44%"
GDBVERSION ?= "16.%"
GLIBCVERSION ?= "2.41%"
LINUXLIBCVERSION ?= "6.12%"
QEMUVERSION ?= "9.2%"
GOVERSION ?= "1.22%"
RUSTVERSION ?= "1.81%"

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
PREFERRED_VERSION_cross-localedef-native   ?= "${GLIBCVERSION}"

PREFERRED_VERSION_qemu ?= "${QEMUVERSION}"
PREFERRED_VERSION_qemu-native ?= "${QEMUVERSION}"
PREFERRED_VERSION_nativesdk-qemu ?= "${QEMUVERSION}"

# Bootstrap Go using a binary release from golang.org.  If you want to bootstrap
# from source using the C-implemented Go 1.4 (only supports x86-64 hosts) then use
# go-native.
PREFERRED_PROVIDER_go-native ?= "go-binary-native"
PREFERRED_VERSION_virtual/${TARGET_PREFIX}go ?= "${GOVERSION}"
PREFERRED_VERSION_go-cross-${TUNE_PKGARCH} ?= "${GOVERSION}"
PREFERRED_VERSION_go-crosssdk-${SDK_ARCH} ?= "${GOVERSION}"
PREFERRED_VERSION_go-cross-canadian-${TRANSLATED_TARGET_ARCH} ?= "${GOVERSION}"
PREFERRED_VERSION_go ?= "${GOVERSION}"
PREFERRED_VERSION_go-native ?= "${GOVERSION}"
PREFERRED_VERSION_go-runtime ?= "${GOVERSION}"
PREFERRED_VERSION_nativesdk-go ?= "${GOVERSION}"
PREFERRED_VERSION_nativesdk-go-runtime ?= "${GOVERSION}"

# Rust toolchain preferred versions:

PREFERRED_VERSION_cargo ?= "${RUSTVERSION}"
PREFERRED_VERSION_cargo-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_libstd-rs ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-cross-${TARGET_ARCH} ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-llvm-native ?= "${RUSTVERSION}"
PREFERRED_VERSION_rust-native ?= "${RUSTVERSION}"

