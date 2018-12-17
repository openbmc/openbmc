#
# Default virtual providers
#
PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
PREFERRED_PROVIDER_virtual/xserver-xf86 ?= "xserver-xorg"
PREFERRED_PROVIDER_virtual/egl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa"
PREFERRED_PROVIDER_virtual/mesa ?= "mesa"
PREFERRED_PROVIDER_virtual/update-alternatives ?= "opkg-utils"
PREFERRED_PROVIDER_virtual/update-alternatives-native ?= "opkg-utils-native"
PREFERRED_PROVIDER_virtual/libx11 ?= "libx11"
PREFERRED_PROVIDER_virtual/base-utils ?= "busybox"
PREFERRED_PROVIDER_xf86-video-intel ?= "xf86-video-intel"
PREFERRED_PROVIDER_virtual/make ?= "make"
PREFERRED_PROVIDER_virtual/make-native ?= "make-native"

#
# Default virtual runtime providers
#
VIRTUAL-RUNTIME_update-alternatives ?= "update-alternatives-opkg"
VIRTUAL-RUNTIME_apm ?= "apm"
VIRTUAL-RUNTIME_alsa-state ?= "alsa-state"
VIRTUAL-RUNTIME_getopt ?= "util-linux-getopt"
VIRTUAL-RUNTIME_base-utils ?= "busybox"
VIRTUAL-RUNTIME_base-utils-hwclock ?= "busybox-hwclock"
VIRTUAL-RUNTIME_base-utils-syslog ?= "busybox-syslog"

#
# Default recipe providers
#
PREFERRED_PROVIDER_dbus-glib ?= "dbus-glib"
PREFERRED_PROVIDER_dbus-glib-native ?= "dbus-glib-native"
PREFERRED_PROVIDER_gdk-pixbuf ?= "gdk-pixbuf"
PREFERRED_PROVIDER_libgcc ?= "libgcc"
PREFERRED_PROVIDER_nativesdk-libgcc ?= "nativesdk-libgcc"
PREFERRED_PROVIDER_linux-libc-headers ?= "linux-libc-headers"
PREFERRED_PROVIDER_nativesdk-linux-libc-headers ?= "nativesdk-linux-libc-headers"
PREFERRED_PROVIDER_matchbox-panel ?= "matchbox-panel-2"
PREFERRED_PROVIDER_opkg ?= "opkg"
PREFERRED_PROVIDER_opkg-native ?= "opkg-native"
PREFERRED_PROVIDER_nativesdk-opkg ?= "nativesdk-opkg"
PREFERRED_PROVIDER_console-tools ?= "kbd"
PREFERRED_PROVIDER_udev ?= "${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd','eudev',d)}"
PREFERRED_RPROVIDER_bluez-hcidump ?= "${@bb.utils.contains('DISTRO_FEATURES','bluetooth bluez5','bluez5','bluez-hcidump',d)}"
# Alternative is ltp-ddt in meta-oe: meta-oe/recipes-devtools/ltp-ddt/ltp-ddt_0.0.4.bb
PREFERRED_PROVIDER_ltp ?= "ltp"
PREFERRED_PROVIDER_getopt ?= "util-linux-getopt"
PREFERRED_PROVIDER_openssl ?= "openssl"
PREFERRED_PROVIDER_openssl-native ?= "openssl-native"
PREFERRED_PROVIDER_nativesdk-openssl ?= "nativesdk-openssl"
PREFERRED_PROVIDER_pkgconfig ?= "pkgconfig"
PREFERRED_PROVIDER_nativesdk-pkgconfig ?= "nativesdk-pkgconfig"
PREFERRED_PROVIDER_pkgconfig-native ?= "pkgconfig-native"
PREFERRED_RPROVIDER_initd-functions ?= "initscripts"
