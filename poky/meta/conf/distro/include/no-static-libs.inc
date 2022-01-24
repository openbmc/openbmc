DISABLE_STATIC ?= " --disable-static"

# qemu aborts on unrecognised option
DISABLE_STATIC:pn-qemu = ""
DISABLE_STATIC:pn-qemu-native = ""
DISABLE_STATIC:pn-nativesdk-qemu = ""
DISABLE_STATIC:pn-qemu-system-native = ""
# needed by gdb
DISABLE_STATIC:pn-readline = ""
# openjade/sgml-common have build issues without static libs
DISABLE_STATIC:pn-sgml-common-native = ""
DISABLE_STATIC:pn-openjade-native = ""
# openssl has build issues without static libs
DISABLE_STATIC:pn-openssl = ""
DISABLE_STATIC:pn-openssl-native = ""
DISABLE_STATIC:pn-nativesdk-openssl = ""
# libssp-static-dev included in build-appliance
DISABLE_STATIC:pn-gcc-runtime = ""
# libusb1-native is used to build static dfu-util-native
DISABLE_STATIC:pn-libusb1-native = ""

EXTRA_OECONF:append = "${DISABLE_STATIC}"

EXTRA_OECMAKE:append:pn-libical = " -DSHARED_ONLY=True"
EXTRA_OECMAKE:append:pn-libjpeg-turbo = " -DENABLE_STATIC=False"
EXTRA_OECMAKE:append:pn-libjpeg-turbo-native = " -DENABLE_STATIC=False"

EXCONFIG_ARGS:append:pn-ncurses = " --without-normal"
EXCONFIG_ARGS:append:pn-ncurses-native = " --without-normal"
EXCONFIG_ARGS:append:pn-nativesdk-ncurses = " --without-normal"
