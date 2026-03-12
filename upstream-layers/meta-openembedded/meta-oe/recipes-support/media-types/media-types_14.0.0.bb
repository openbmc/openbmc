SECTION = "base"
SUMMARY = "MIME files 'mime.types'"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=f3ace4a4ff8aa9e374be6080e41a822e"
DEPENDS = "file"
RDEPENDS:${PN} = "perl"
RRECOMMENDS:${PN} = "file"

SRC_URI = "${DEBIAN_MIRROR}/main/m/${BPN}/${BPN}_${PV}.tar.xz"
SRC_URI[sha256sum] = "d5877ac8c1fa3661b3c9ace293ae154c844fad686786f085e954c014f7e73f4d"
S = "${UNPACKDIR}/work"

inherit update-alternatives

FILES:${PN} += " ${datadir}/bug/media-types"

docdir:append = "/${BPN}"

do_install () {
    install -d ${D}${sysconfdir}
    install -d ${D}${datadir}/bug/media-types
    install -d ${D}${docdir}
    install -m 644 mime.types         ${D}${sysconfdir}/
    install -m 644 debian/bug-presubj ${D}${datadir}/bug/media-types/presubj
    install -m 644 debian/changelog   ${D}${docdir}/changelog
    install -m 644 debian/copyright   ${D}${docdir}/copyright
    cd ${D}${docdir}; gzip -9v changelog
}

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN} = "mime.types"
ALTERNATIVE_LINK_NAME[mime.types] = "${sysconfdir}/mime.types"

INHIBIT_DEFAULT_DEPS = "1"

# Debian used to have mime-support package which was media-types and
# mailcap recipes in one.
PACKAGES += "mime-support"
RPROVIDES:${PN} += "mime-support"
RDEPENDS:mime-support += "${PN} mailcap"
ALLOW_EMPTY:mime-support = "1"
