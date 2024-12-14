#
# Picolibc configuration
#

LIBCEXTENSION = "-picolibc"
LIBCOVERRIDE = ":libc-picolibc"

PREFERRED_PROVIDER_virtual/libc ?= "picolibc"
PREFERRED_PROVIDER_virtual/libiconv ?= "picolibc"
PREFERRED_PROVIDER_virtual/libintl ?= "picolibc"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"

DISTRO_FEATURES_BACKFILL_CONSIDERED += "ldconfig"

IMAGE_LINGUAS = ""

LIBC_DEPENDENCIES = " \
    picolibc-dbg \
    picolibc-dev \
    libgcc-dev \
    libgcc-dbg \
    libstdc++-dev  \
    libstdc++-staticdev \
"

ASSUME_PROVIDED += "virtual/crypt"

TARGET_OS = "elf"
TARGET_OS:arm = "eabi"

TOOLCHAIN_HOST_TASK ?= "packagegroup-cross-canadian-${MACHINE} nativesdk-qemu nativesdk-sdk-provides-dummy"
TOOLCHAIN_TARGET_TASK ?= "${LIBC_DEPENDENCIES}"
TOOLCHAIN_NEED_CONFIGSITE_CACHE:remove = "zlib ncurses"

# RISCV linker doesnt support PIE
SECURITY_CFLAGS:libc-picolibc:qemuriscv32 = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_CFLAGS:libc-picolibc:qemuriscv64 = "${SECURITY_NOPIE_CFLAGS}"


