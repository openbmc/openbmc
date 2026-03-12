# An overview of Y2038 issues, and how to prepare a yocto-based
# product for that date is available at
# https://www.youtube.com/watch?v=eaOHJHFMobw
# https://osseu2024.sched.com/event/1ej4C/
#
# To simulate Y2038 occurring in qemu, add to your build configuration:
# QB_OPT_APPEND:append = " -rtc base=2040-02-02"
#
# Note that this does result in ptest failures on qemux86:
#
#   (none known at this point)
#
# and some ptest failures appear in qemux86-64 as well:
#
#   openssl https://github.com/openssl/openssl/issues/21671
#
# Working to address those (before Y2038 rolls in) will be appreciated.
#
# Note: some issues occur on 32 bit regardless of the date set:
#
#   lttng-tools:
#   https://bugs.lttng.org/issues/1436
#   FAIL: tests.serial 59 - Test script 'ust/blocking/test_blocking' returned code 255.
#   (due to lack of space on /tmp, which is due to a non-highmem kernel, which allocates only 800M of RAM to userspace)
#

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
GLIBC_64BIT_TIME_FLAGS:pn-glibc-testsuite = ""

# Caused by the flags exceptions above
INSANE_SKIP:append:pn-glibc = " 32bit-time"

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
