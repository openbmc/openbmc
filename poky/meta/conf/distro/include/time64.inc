# To simulate Y2038 occurring in qemu, add to your build configuration:
# QB_OPT_APPEND:append = " -rtc base=2040-02-02"
#
# Note that this does result in ptest failures on qemux86:
# perl python3 dbus openssl glibc-tests openssh curl glib-2.0 tcl libmodule-build-perl
# and a subset of those occurs in qemux86-64 as well:
# curl python3 openssl openssl tcl python3-cryptography
#
# Working to address those (before Y2038 rolls in) will be appreciated.

GLIBC_64BIT_TIME_FLAGS = "${GLIBC_64BIT_TIME_FLAGS_WHEN_NEEDED}"

# Only needed for some 32-bit architectures, some relatively newer
# architectures do not need it ( e.g. riscv32 )
GLIBC_64BIT_TIME_FLAGS_WHEN_NEEDED = " -D_TIME_BITS=64 -D_FILE_OFFSET_BITS=64"
TARGET_CC_ARCH:append:arm = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:armeb = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:mipsarcho32 = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:powerpc = "${@bb.utils.contains('TUNE_FEATURES', 'm32', '${GLIBC_64BIT_TIME_FLAGS}', '', d)}"
TARGET_CC_ARCH:append:x86 = "${@bb.utils.contains('TUNE_FEATURES', 'm32', '${GLIBC_64BIT_TIME_FLAGS}', '', d)}"

GLIBC_64BIT_TIME_FLAGS:pn-glibc = ""
GLIBC_64BIT_TIME_FLAGS:pn-glibc-y2038-tests = ""
GLIBC_64BIT_TIME_FLAGS:pn-glibc-testsuite = ""
# pipewire-v4l2 explicitly sets _FILE_OFFSET_BITS=32 to get access to
# both 32 and 64 bit file APIs.  But it does not handle the time side?
# Needs further investigation
GLIBC_64BIT_TIME_FLAGS:pn-pipewire = ""
# Undefines _FILE_OFFSET_BITS on purpose in
# libsanitizer/sanitizer_common/sanitizer_platform_limits_posix.cpp
GLIBC_64BIT_TIME_FLAGS:pn-gcc-sanitizers = ""

# Caused by the flags exceptions above
INSANE_SKIP:append:pn-gcc-sanitizers = " 32bit-time"
INSANE_SKIP:append:pn-glibc = " 32bit-time"
INSANE_SKIP:append:pn-glibc-y2038-tests = " 32bit-time"

# Strace has tests that call 32 bit API directly, which is fair enough, e.g.
# /usr/lib/strace/ptest/tests/ioctl_termios uses 32-bit api 'ioctl'
INSANE_SKIP:append:pn-strace = " 32bit-time"

# Pseudo has to wrap all glibc calls including the 32 bit ones even
# if it doesn't use them itself
INSANE_SKIP:append:pn-pseudo = " 32bit-time"

# Additionally cargo_common class (i.e. everything written in rust)
# has the same INSANE_SKIP setting.
# Please check the comment in meta/classes-recipe/cargo_common.bbclass
# for information about why, and the overall Y2038 situation in rust.

# binutils/gcc/gdb still need fixing for qemuarm/qemuarmv5/qemumips/qemuppc/qemux86
#binutils-cross-canadian-arm-2.42-r0 do_package_qa: QA Issue: xxx/x86_64-oesdk-linux/usr/bin/arm-oe-linux-gnueabi/arm-oe-linux-gnueabi-ranlib uses 32-bit api 'lstat'
INSANE_SKIP:append:pn-binutils-cross-canadian-${TRANSLATED_TARGET_ARCH} = " 32bit-time"
#gcc-cross-canadian-arm-14.1.0-r0 do_package_qa: QA Issue: xxx/x86_64-oesdk-linux/usr/libexec/arm-oe-linux-gnueabi/gcc/arm-oe-linux-gnueabi/14.1.0/plugin/gengtype uses 32-bit api 'ctime'
INSANE_SKIP:append:pn-gcc-cross-canadian-${TRANSLATED_TARGET_ARCH} = " 32bit-time"
#gdb-cross-canadian-arm-16.1-r0 do_package_qa: QA Issue: xxx/x86_64-oesdk-linux/usr/bin/arm-oe-linux-gnueabi/arm-oe-linux-gnueabi-gdb uses 32-bit api 'lstat'
INSANE_SKIP:append:pn-gdb-cross-canadian-${TRANSLATED_TARGET_ARCH} = " 32bit-time"
