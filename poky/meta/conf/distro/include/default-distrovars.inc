QA_LOGFILE = "${TMPDIR}/qa.log"

OEINCLUDELOGS ?= "yes"
KERNEL_CONSOLE ?= "ttyS0"
KEEPUIMAGE ??= "yes"

IMAGE_LINGUAS ?= "en-us en-gb"
ENABLE_BINARY_LOCALE_GENERATION ?= "1"
LOCALE_UTF8_ONLY ?= "0"
LOCALE_UTF8_IS_DEFAULT ?= "1"
LOCALE_UTF8_IS_DEFAULT_class-nativesdk = "0"

DISTRO_FEATURES_DEFAULT ?= "acl alsa argp bluetooth ext2 ipv4 ipv6 largefile pcmcia usbgadget usbhost wifi xattr nfs zeroconf pci 3g nfc x11 vfat"
DISTRO_FEATURES ?= "${DISTRO_FEATURES_DEFAULT}"
IMAGE_FEATURES ?= ""

WHITELIST_GPL-3.0 ?= ""

COMMERCIAL_AUDIO_PLUGINS ?= ""
# COMMERCIAL_AUDIO_PLUGINS ?= "gst-plugins-ugly-mad gst-plugins-ugly-mpegaudioparse"
COMMERCIAL_VIDEO_PLUGINS ?= ""
# COMMERCIAL_VIDEO_PLUGINS ?= "gst-plugins-ugly-mpeg2dec gst-plugins-ugly-mpegstream gst-plugins-bad-mpegvideoparse"
# Set of common licenses used for license.bbclass
COMMON_LICENSE_DIR ??= "${COREBASE}/meta/files/common-licenses"

BB_GENERATE_MIRROR_TARBALLS ??= "0"

NO32LIBS ??= "1"

# Default to emitting logfiles if a build fails.
BBINCLUDELOGS ??= "yes"
SDK_VERSION ??= "nodistro.0"
DISTRO_VERSION ??= "nodistro.0"

# Missing checksums should raise an error
BB_STRICT_CHECKSUM = "1"

GTK2DISTROFEATURES = "directfb x11"
GTK3DISTROFEATURES = "x11 wayland"

ARCH_DEFAULT_KERNELIMAGETYPE = "zImage"
ARCH_DEFAULT_KERNELIMAGETYPE_x86 = "bzImage"
ARCH_DEFAULT_KERNELIMAGETYPE_x86-64 = "bzImage"
KERNEL_IMAGETYPE ??= "${ARCH_DEFAULT_KERNELIMAGETYPE}"
KERNEL_IMAGETYPES ??= "${KERNEL_IMAGETYPE}"

# The CONNECTIVITY_CHECK_URIS are used to test whether we can succesfully
# fetch from the network (and warn you if not). To disable the test set
# the variable to be empty.
# Git example url: git://git.yoctoproject.org/yocto-firewall-test;protocol=git;rev=master
CONNECTIVITY_CHECK_URIS ?= "https://www.example.com/"
