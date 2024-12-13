SUMMARY = "Resource Public Key Infrastructure (RPKI) daemon"
HOMEPAGE = "https://www.nlnetlabs.nl/projects/rpki/krill/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

DEPENDS = "openssl"

# SRC_URI += "crate://crates.io/krill/0.9.1"
SRC_URI = "git://github.com/NLnetLabs/krill.git;protocol=https;branch=main"
SRCREV = "e92098419c7ad82939e0483bc76df21eff705b80"
SRC_URI += "file://panic_workaround.patch"

include krill-crates.inc

UPSTREAM_CHECK_URI = "https://github.com/NLnetLabs/${BPN}/releases"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${UNPACKDIR}/git"
CARGO_SRC_DIR = ""

inherit pkgconfig useradd systemd cargo cargo-update-recipe-crates

do_install:append () {
    install -d ${D}${sysconfdir}
    install -d ${D}${datadir}/krill

    install -m 664 ${S}/defaults/krill.conf ${D}${sysconfdir}/.
    install ${S}/defaults/* ${D}${datadir}/krill/.
    find  ${D}${bindir}/ -name "krill*"  -exec sed -i -e 's#${CARGO_HOME}/bitbake##g' {} +
}

KRILL_UID ?= "krill"
KRILL_GID ?= "krill"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${KRILL_UID}"
USERADD_PARAM:${PN} = "--system -g ${KRILL_GID} --home-dir  \
                       /var/lib/krill/ --no-create-home  \
                       --shell /sbin/nologin ${BPN}"

FILES:${PN} += "{sysconfdir}/defaults ${datadir}"
INSANE_SKIP:${PN} = "already-stripped"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64).*-linux"
