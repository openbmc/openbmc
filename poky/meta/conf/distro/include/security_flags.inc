# Setup extra CFLAGS and LDFLAGS which have 'security' benefits. These
# don't work universally, there are recipes which can't use one, the other
# or both so a blacklist is maintained here. The idea would be over
# time to reduce this list to nothing.
# From a Yocto Project perspective, this file is included and tested
# in the DISTRO="poky" configuration.

GCCPIE ?= "--enable-default-pie"
# If static PIE is known to work well, GLIBCPIE="--enable-static-pie" can be set

# _FORTIFY_SOURCE requires -O1 or higher, so disable in debug builds as they use
# -O0 which then results in a compiler warning.
OPTLEVEL = "${@bb.utils.filter('SELECTED_OPTIMIZATION', '-O0 -O1 -O2 -O3 -Ofast -Og -Os -Oz -O', d)}"

lcl_maybe_fortify ?= "${@oe.utils.conditional('OPTLEVEL','-O0','','${OPTLEVEL} -D_FORTIFY_SOURCE=2',d)}"

# Error on use of format strings that represent possible security problems
SECURITY_STRINGFORMAT ?= "-Wformat -Wformat-security -Werror=format-security"

# Inject pie flags into compiler flags if not configured with gcc itself
# especially useful with external toolchains
SECURITY_PIE_CFLAGS ?= "${@'' if '${GCCPIE}' else '-pie -fPIE'}"

SECURITY_NOPIE_CFLAGS ?= "-no-pie -fno-PIE"

SECURITY_STACK_PROTECTOR ?= "-fstack-protector-strong"

SECURITY_CFLAGS ?= "${SECURITY_STACK_PROTECTOR} ${SECURITY_PIE_CFLAGS} ${lcl_maybe_fortify} ${SECURITY_STRINGFORMAT}"
SECURITY_NO_PIE_CFLAGS ?= "${SECURITY_STACK_PROTECTOR} ${lcl_maybe_fortify} ${SECURITY_STRINGFORMAT}"

SECURITY_LDFLAGS ?= "-Wl,-z,relro,-z,now"
SECURITY_X_LDFLAGS ?= "-Wl,-z,relro"

# powerpc does not get on with pie for reasons not looked into as yet
GCCPIE_powerpc = ""
GLIBCPIE_powerpc = ""
SECURITY_CFLAGS_remove_powerpc = "${SECURITY_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libgcc_powerpc = ""

SECURITY_CFLAGS_pn-glibc = ""
SECURITY_CFLAGS_pn-glibc-testsuite = ""
SECURITY_CFLAGS_pn-gcc-runtime = ""
SECURITY_CFLAGS_pn-grub = ""
SECURITY_CFLAGS_pn-grub-efi = ""
SECURITY_CFLAGS_pn-mkelfimage_x86 = ""

SECURITY_CFLAGS_pn-valgrind = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS_pn-valgrind = ""
SECURITY_CFLAGS_pn-sysklogd = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS_pn-sysklogd = ""

# Recipes which fail to compile when elevating -Wformat-security to an error
SECURITY_STRINGFORMAT_pn-busybox = ""
SECURITY_STRINGFORMAT_pn-gcc = ""

TARGET_CC_ARCH_append_class-target = " ${SECURITY_CFLAGS}"
TARGET_LDFLAGS_append_class-target = " ${SECURITY_LDFLAGS}"
TARGET_CC_ARCH_append_class-cross-canadian = " ${SECURITY_CFLAGS}"
TARGET_LDFLAGS_append_class-cross-canadian = " ${SECURITY_LDFLAGS}"

SECURITY_STACK_PROTECTOR_pn-gcc-runtime = ""
SECURITY_STACK_PROTECTOR_pn-glibc = ""
SECURITY_STACK_PROTECTOR_pn-glibc-testsuite = ""
# All xorg module drivers need to be linked this way as well and are
# handled in recipes-graphics/xorg-driver/xorg-driver-common.inc
SECURITY_LDFLAGS_pn-xserver-xorg = "${SECURITY_X_LDFLAGS}"

TARGET_CC_ARCH_append_pn-binutils = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-gcc = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-gdb = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-perf = " ${SELECTED_OPTIMIZATION}"
