require pkgconfig.inc

SRC_URI += " \
            file://pkg-config-native.in \
            file://fix-glib-configure-libtool-usage.patch \
           "
