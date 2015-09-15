# Setup extra CFLAGS and LDFLAGS which have 'security' benefits. These 
# don't work universally, there are recipes which can't use one, the other
# or both so a blacklist is maintained here. The idea would be over
# time to reduce this list to nothing.
# From a Yocto Project perspective, this file is included and tested
# in the DISTRO="poky-lsb" configuration.

# _FORTIFY_SOURCE requires -O1 or higher, so disable in debug builds as they use
# -O0 which then results in a compiler warning.
lcl_maybe_fortify = "${@base_conditional('DEBUG_BUILD','1','','-D_FORTIFY_SOURCE=2',d)}"

SECURITY_CFLAGS ?= "-fstack-protector-all -pie -fpie ${lcl_maybe_fortify}"
SECURITY_NO_PIE_CFLAGS ?= "-fstack-protector-all ${lcl_maybe_fortify}"

SECURITY_LDFLAGS ?= "-Wl,-z,relro,-z,now"
SECURITY_X_LDFLAGS ?= "-Wl,-z,relro"

# powerpc does not get on with pie for reasons not looked into as yet
SECURITY_CFLAGS_powerpc = "-fstack-protector-all ${lcl_maybe_fortify}"
# Deal with ppc specific linker failures when using the cflags
SECURITY_CFLAGS_pn-dbus_powerpc = ""
SECURITY_CFLAGS_pn-dbus-ptest_powerpc = ""
SECURITY_CFLAGS_pn-libmatchbox_powerpc = ""

# arm specific security flag issues
SECURITY_CFLAGS_pn-lttng-tools_arm = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-aspell = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-beecrypt = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-blktrace = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-coreutils = "${SECURITY_NO_PIE_CFLAGS}"
# Curl seems to check for FORTIFY_SOURCE in CFLAGS, but even assigned
# to CPPFLAGS it gets picked into CFLAGS in bitbake.
#TARGET_CPPFLAGS_pn-curl += "-D_FORTIFY_SOURCE=2"
SECURITY_CFLAGS_pn-cups = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-curl = "-fstack-protector-all -pie -fpie"
SECURITY_CFLAGS_pn-db = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-directfb = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-glibc = ""
SECURITY_CFLAGS_pn-glibc-initial = ""
SECURITY_CFLAGS_pn-elfutils = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-enchant = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-expect = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-flac = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-flex = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gcc = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gcc-runtime = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gcc-sanitizers = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gdb = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gmp = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gnutls = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-grub = ""
SECURITY_CFLAGS_pn-grub-efi = ""
SECURITY_CFLAGS_pn-grub-efi-native = ""
SECURITY_CFLAGS_pn-grub-efi-x86-native = ""
SECURITY_CFLAGS_pn-grub-efi-i586-native = ""
SECURITY_CFLAGS_pn-grub-efi-x86-64-native = ""
SECURITY_CFLAGS_pn-gst-plugins-bad = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gst-plugins-gl = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gstreamer1.0-plugins-bad = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-gstreamer1.0-plugins-good = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-harfbuzz = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-kexec-tools = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-iptables = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libaio = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libcap = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libgcc = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libid3tag = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libnewt = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libglu = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libpcap = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libpcre = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-libproxy = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-lttng-ust = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-mesa = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-mesa-gl = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-openssl = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-opensp = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-ppp = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python-imaging = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python-pycurl = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python-smartpm = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python-numpy = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-python3 = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-tcl = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-tiff = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-valgrind = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-zlib = "${SECURITY_NO_PIE_CFLAGS}"

# These 2 have text relco errors with the pie options enabled
SECURITY_CFLAGS_pn-ltp = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-pulseaudio = "${SECURITY_NO_PIE_CFLAGS}"

TARGET_CFLAGS_append = " ${SECURITY_CFLAGS}"
TARGET_LDFLAGS_append = " ${SECURITY_LDFLAGS}"

SECURITY_LDFLAGS_pn-xf86-video-fbdev = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-intel = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-omapfb = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-omap = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-vesa = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xf86-video-vmware = "${SECURITY_X_LDFLAGS}"
SECURITY_LDFLAGS_pn-xserver-xorg = "${SECURITY_X_LDFLAGS}"
