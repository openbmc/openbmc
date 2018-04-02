QA_LOGFILE = "${TMPDIR}/qa.log"

OEINCLUDELOGS ?= "yes"
KERNEL_CONSOLE ?= "ttyS0"
KEEPUIMAGE ??= "yes"

IMAGE_LINGUAS ?= "en-us en-gb"
ENABLE_BINARY_LOCALE_GENERATION ?= "1"
LOCALE_UTF8_ONLY ?= "0"
LOCALE_UTF8_IS_DEFAULT ?= "1"
LOCALE_UTF8_IS_DEFAULT_class-nativesdk = "0"

DISTRO_FEATURES_DEFAULT ?= "acl alsa argp bluetooth ext2 irda largefile pcmcia usbgadget usbhost wifi xattr nfs zeroconf pci 3g nfc x11"
DISTRO_FEATURES_LIBC_DEFAULT ?= "ipv4 ipv6 libc-backtrace libc-big-macros libc-bsd libc-cxx-tests libc-catgets libc-charsets libc-crypt \
					libc-crypt-ufc libc-db-aliases libc-envz libc-fcvt libc-fmtmsg libc-fstab libc-ftraverse \
					libc-getlogin libc-idn libc-inet-anl libc-libm libc-locales libc-locale-code \
					libc-memusage libc-nis libc-nsswitch libc-rcmd libc-rtld-debug libc-spawn libc-streams libc-sunrpc \
					libc-utmp libc-utmpx libc-wordexp libc-posix-clang-wchar libc-posix-regexp libc-posix-regexp-glibc \
					libc-posix-wchar-io"
DISTRO_FEATURES_LIBC ?= "${DISTRO_FEATURES_LIBC_DEFAULT}"
DISTRO_FEATURES_LIBC_class-nativesdk = "${DISTRO_FEATURES_LIBC_DEFAULT}"
DISTRO_FEATURES ?= "${DISTRO_FEATURES_DEFAULT} ${DISTRO_FEATURES_LIBC}"

IMAGE_FEATURES ?= ""

WHITELIST_GPL-3.0 ?= ""
LGPLv2_WHITELIST_GPL-3.0 ?= ""

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
