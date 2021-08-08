SUMMARY = "Resource Public Key Infrastructure (RPKI) daemon"
HOMEPAGE = "https://www.nlnetlabs.nl/projects/rpki/krill/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

DEPENDS = "openssl"

include krill.inc

# SRC_URI += "crate://crates.io/krill/0.9.1"
SRC_URI += "git://github.com/NLnetLabs/krill.git;protocol=https;nobranch=1;branch=main"
SRCREV = "d6c03b6f0199b1d10d252750a19a92b84576eb30"

SRC_URI += "file://panic_workaround.patch"

S = "${WORKDIR}/git"
CARGO_SRC_DIR = ""

inherit pkgconfig useradd systemd cargo


do_install:append () {
    install -d ${D}${sysconfdir}
    install -d ${D}${datadir}/krill

    install -m 664 ${S}/defaults/krill.conf ${D}${sysconfdir}/.
    install ${S}/defaults/* ${D}${datadir}/krill/.
}

KRILL_UID ?= "krill"
KRILL_GID ?= "krill"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${KRILL_UID}"
USERADD_PARAM:${PN} = "--system -g ${KRILL_GID} --home-dir  \
                       /var/lib/krill/ --no-create-home  \
                       --shell /sbin/nologin ${BPN}"

FILES:${PN} += "{sysconfdir}/defaults ${datadir}"
