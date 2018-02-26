# Setup extra CFLAGS and LDFLAGS which have 'security' benefits. These
# don't work universally, there are recipes which can't use one, the other
# or both so a blacklist is maintained here. The idea would be over
# time to reduce this list to nothing.
# From a Yocto Project perspective, this file is included and tested
# in the DISTRO="poky-lsb" configuration.

GCCPIE ?= "--enable-default-pie"

# _FORTIFY_SOURCE requires -O1 or higher, so disable in debug builds as they use
# -O0 which then results in a compiler warning.
lcl_maybe_fortify = "${@base_conditional('DEBUG_BUILD','1','','-D_FORTIFY_SOURCE=2',d)}"

# Error on use of format strings that represent possible security problems
SECURITY_STRINGFORMAT ?= "-Wformat -Wformat-security -Werror=format-security"

# Inject pie flags into compiler flags if not configured with gcc itself
# especially useful with external toolchains
SECURITY_PIE_CFLAGS ?= "${@'' if '${GCCPIE}' else '-pie -fPIE'}"

SECURITY_NOPIE_CFLAGS ?= "-no-pie -fno-PIE"

SECURITY_CFLAGS ?= "-fstack-protector-strong ${SECURITY_PIE_CFLAGS} ${lcl_maybe_fortify} ${SECURITY_STRINGFORMAT}"
SECURITY_NO_PIE_CFLAGS ?= "-fstack-protector-strong ${lcl_maybe_fortify} ${SECURITY_STRINGFORMAT}"

SECURITY_LDFLAGS ?= "-fstack-protector-strong -Wl,-z,relro,-z,now"
SECURITY_X_LDFLAGS ?= "-fstack-protector-strong -Wl,-z,relro"

# powerpc does not get on with pie for reasons not looked into as yet
SECURITY_CFLAGS_powerpc = "-fstack-protector-strong ${lcl_maybe_fortify} ${SECURITY_NOPIE_CFLAGS}"
SECURITY_CFLAGS_pn-libgcc_powerpc = ""
GCCPIE_powerpc = ""

# arm specific security flag issues
SECURITY_CFLAGS_pn-glibc = ""
SECURITY_CFLAGS_pn-glibc-initial = ""
SECURITY_CFLAGS_pn-gcc-runtime = ""
SECURITY_CFLAGS_pn-grub = ""
SECURITY_CFLAGS_pn-grub-efi = ""
SECURITY_CFLAGS_pn-grub-efi-native = ""
SECURITY_CFLAGS_pn-grub-efi-x86-native = ""
SECURITY_CFLAGS_pn-grub-efi-i586-native = ""
SECURITY_CFLAGS_pn-grub-efi-x86-64-native = ""

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

SECURITY_LDFLAGS_remove_pn-gcc-runtime = "-fstack-protector-strong"
SECURITY_LDFLAGS_remove_pn-glibc = "-fstack-protector-strong"
SECURITY_LDFLAGS_remove_pn-glibc-initial = "-fstack-protector-strong"
SECURITY_LDFLAGS_pn-xf86-video-fbdev = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-intel = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-omapfb = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-omap = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-vesa = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-vmware = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xserver-xorg = "${SECURITY_X_LDFLAGS}"

TARGET_CC_ARCH_append_pn-binutils = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-gcc = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-gdb = " ${SELECTED_OPTIMIZATION}"
TARGET_CC_ARCH_append_pn-perf = " ${SELECTED_OPTIMIZATION}"
