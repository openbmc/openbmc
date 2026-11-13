DESCRIPTION = "CUPS polkit helper"
HOMEPAGE = "https://www.freedesktop.org/software/cups-pk-helper/releases/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "cups polkit glib-2.0"

inherit meson pkgconfig gettext features_check

REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI = " \
    https://www.freedesktop.org/software/cups-pk-helper/releases/cups-pk-helper-${PV}.tar.xz \
    file://dont-override-gettextdatadirs.patch \
"
SRC_URI[sha256sum] = "66070ddb448fe9fcee76aa26be2ede5a80f85563e3a4afd59d2bfd79fbe2e831"

FILES:${PN} += "${datadir}"
