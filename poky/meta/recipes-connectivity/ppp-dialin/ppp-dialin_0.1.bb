SUMMARY = "Enables PPP dial-in through a serial connection"
SECTION = "console/network"
DESCRIPTION = "PPP dail-in provides a point to point protocol (PPP), so that other computers can dial up to it and access connected networks."
DEPENDS = "ppp"
RDEPENDS:${PN} = "ppp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://host-peer \
           file://ppp-dialin"

inherit allarch useradd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
	install -d ${D}${sysconfdir}/ppp/peers
	install -m 0644 ${S}/host-peer ${D}${sysconfdir}/ppp/peers/host

	install -d ${D}${sbindir}
	install -m 0755 ${S}/ppp-dialin ${D}${sbindir}
}

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home /dev/null \
                       --no-create-home --shell ${sbindir}/ppp-dialin \
                       --no-user-group --gid nogroup ppp"
