DISABLE_STATIC ?= " --disable-static"

# qemu aborts on unrecognised option
DISABLE_STATIC_pn-qemu = ""
DISABLE_STATIC_pn-qemu-native = ""
DISABLE_STATIC_pn-nativesdk-qemu = ""
DISABLE_STATIC_pn-qemu-system-native = ""
# pciutils fails build
DISABLE_STATIC_pn-pciutils = ""
# libcap aborts on unrecognised option
DISABLE_STATIC_pn-libcap = ""
DISABLE_STATIC_pn-libcap-native = ""
DISABLE_STATIC_pn-nativesdk-libcap = ""
# libpcap aborts on unrecognised option
DISABLE_STATIC_pn-libpcap = ""
# needed by gdb
DISABLE_STATIC_pn-readline = ""
# openjade/sgml-common have build issues without static libs
DISABLE_STATIC_pn-sgml-common-native = ""
DISABLE_STATIC_pn-openjade-native = ""
# openssl has build issues without static libs
DISABLE_STATIC_pn-openssl = ""
DISABLE_STATIC_pn-openssl-native = ""
DISABLE_STATIC_pn-nativesdk-openssl = ""
# libssp-static-dev included in build-appliance
DISABLE_STATIC_pn-gcc-runtime = ""
# libusb1-native is used to build static dfu-util-native
DISABLE_STATIC_pn-libusb1-native = ""

EXTRA_OECONF_append = "${DISABLE_STATIC}"

EXTRA_OECMAKE_append_pn-libical = " -DSHARED_ONLY=True"
EXTRA_OECMAKE_append_pn-libjpeg-turbo = " -DENABLE_STATIC=False"

EXCONFIG_ARGS_append_pn-ncurses = " --without-normal"
EXCONFIG_ARGS_append_pn-ncurses-native = " --without-normal"
EXCONFIG_ARGS_append_pn-nativesdk-ncurses = " --without-normal"
