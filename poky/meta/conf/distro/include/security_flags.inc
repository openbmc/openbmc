# Setup extra CFLAGS and LDFLAGS which have 'security' benefits. These
# don't work universally, there are recipes which can't use one, the other
# or both so an override is maintained here. The idea would be over
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
GCCPIE:powerpc = ""
GLIBCPIE:powerpc = ""
SECURITY_CFLAGS:remove:powerpc = "${SECURITY_PIE_CFLAGS}"
SECURITY_CFLAGS:pn-libgcc:powerpc = ""

SECURITY_CFLAGS:pn-glibc = ""
SECURITY_CFLAGS:pn-glibc-testsuite = ""
SECURITY_CFLAGS:pn-gcc-runtime = ""
SECURITY_CFLAGS:pn-grub = ""
SECURITY_CFLAGS:pn-grub-efi = ""
SECURITY_CFLAGS:pn-mkelfimage:x86 = ""

SECURITY_CFLAGS:pn-valgrind = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS:pn-valgrind = ""
SECURITY_CFLAGS:pn-sysklogd = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_LDFLAGS:pn-sysklogd = ""

# Recipes which fail to compile when elevating -Wformat-security to an error
SECURITY_STRINGFORMAT:pn-busybox = ""
SECURITY_STRINGFORMAT:pn-gcc = ""

TARGET_CC_ARCH:append:class-target = " ${SECURITY_CFLAGS}"
TARGET_LDFLAGS:append:class-target = " ${SECURITY_LDFLAGS}"
TARGET_CC_ARCH:append:class-cross-canadian = " ${SECURITY_CFLAGS}"
TARGET_LDFLAGS:append:class-cross-canadian = " ${SECURITY_LDFLAGS}"

SECURITY_STACK_PROTECTOR:pn-gcc-runtime = ""
SECURITY_STACK_PROTECTOR:pn-glibc = ""
SECURITY_STACK_PROTECTOR:pn-glibc-testsuite = ""
SECURITY_STACK_PROTECTOR:pn-ltp = ""
# All xorg module drivers need to be linked this way as well and are
# handled in recipes-graphics/xorg-driver/xorg-driver-common.inc
SECURITY_LDFLAGS:pn-xserver-xorg = "${SECURITY_X_LDFLAGS}"

TARGET_CC_ARCH:append:pn-binutils = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH:append:pn-gcc = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH:append:pn-gdb = " ${SELECTED_OPTIMIZATION}"
