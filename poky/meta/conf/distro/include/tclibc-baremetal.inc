#
# baremetal configuration
#

LIBCEXTENSION = ""

LIBCOVERRIDE = ":libc-baremetal"

ASSUME_PROVIDED += "virtual/libc virtual/libiconv virtual/crypt"

PREFERRED_PROVIDER_virtual/libc ?= "musl"
PREFERRED_PROVIDER_virtual/libiconv ?= "musl"
PREFERRED_PROVIDER_virtual/libintl ?= "gettext"
PREFERRED_PROVIDER_virtual/nativesdk-libintl ?= "nativesdk-glibc"
PREFERRED_PROVIDER_virtual/nativesdk-libiconv ?= "nativesdk-glibc"

USE_NLS ?= "no"
IMAGE_LINGUAS = ""
LIBC_DEPENDENCIES = ""

EXTRA_OECONF:pn-gcc-cross-${TARGET_ARCH}:append = " --without-headers"
DEPENDS:remove:pn-meta-toolchain = "virtual/libc virtual/compilerlibs"

# certain compiler libs cannot be used without libc, avoid the dependence on compilerlibs
BASEDEPENDS:remove:class-target = "virtual/compilerlibs"

TARGET_OS = "elf"
TARGET_OS:arm = "eabi"

TOOLCHAIN_HOST_TASK ?= "packagegroup-cross-canadian-${MACHINE} nativesdk-qemu nativesdk-sdk-provides-dummy"
TOOLCHAIN_HOST_TASK_ATTEMPTONLY ?= ""
TOOLCHAIN_TARGET_TASK ?= "libgcc-dev"
TOOLCHAIN_NEED_CONFIGSITE_CACHE:remove = "virtual/${MLPREFIX}libc zlib ncurses"

# disable stack protector by default (no-libc, no protector implementation)
SECURITY_STACK_PROTECTOR:libc-baremetal = ""
# disable pie security flags by default
SECURITY_CFLAGS:libc-baremetal = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS:libc-baremetal = ""
