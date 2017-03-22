LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://github.com/systemd/systemd-bootchart.git;protocol=https"

# Modify these as desired
PV = "230+git${SRCPV}"
SRCREV = "dcb246b46239259ec99b3c6e6ab7d27e1a5a8a84"

S = "${WORKDIR}/git"

DEPENDS = "systemd libxslt-native xmlto-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native intltool"

inherit pkgconfig autotools systemd distro_features_check

REQUIRED_DISTRO_FEATURES = "systemd"

SYSTEMD_SERVICE_${PN} = "systemd-bootchart.service"

do_configure_prepend() {
    # intltool.m4 is a soft link to /usr/share/aclocal/m4, delete it and use the one in our sysroot
    rm -f ${S}/m4/intltool.m4
}

FILES_${PN} += "${systemd_unitdir}/systemd-bootchart"

EXTRA_OECONF = " --with-rootprefix=${base_prefix} \
                 --with-rootlibdir=${base_libdir}"
