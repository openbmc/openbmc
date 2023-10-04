
SUMMARY = "Services for periodic btrfs maintenance tasks"
DESCRIPTION = "A set of scripts supplementing the btrfs filesystem and aims \
    to automate a few maintenance tasks. This means the scrub, balance, trim \
    or defragmentation."
HOMEPAGE = "https://github.com/kdave/btrfsmaintenance"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

SECTION = "base"

SRC_URI = "git://github.com/kdave/${BPN};branch=master;protocol=https \
    file://0001-change-sysconfig-path-to-etc-default.patch \
    file://0002-add-WantedBy-directive-to-btrfsmaintenance-refresh.s.patch \
"
SRCREV = "be42cb6267055d125994abd6927cf3a26deab74c"

UPSTREAM_CHECK_URI = "https://github.com/kdave/${BPN}/tags"
UPSTREAM_CHECK_REGEX = "${BPN}/releases/tag/v(?P<pver>\d+(?:\.\d+)*)"

RDEPENDS:${PN} = "bash btrfs-tools"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -Dm0644 ${S}/btrfsmaintenance-refresh.path \
        ${D}${systemd_system_unitdir}/btrfsmaintenance-refresh.path
    install -Dm0644 ${S}/*.timer \
        ${D}${systemd_system_unitdir}
    install -Dm0644 ${S}/*.service \
        ${D}${systemd_system_unitdir}

    install -Dm0644 ${S}/btrfsmaintenance-functions \
        ${D}${datadir}/${BPN}/btrfsmaintenance-functions
    install -Dm0755 ${S}/*.sh \
        ${D}${datadir}/${BPN}

    install -Dm0644 ${S}/sysconfig.btrfsmaintenance \
        ${D}${sysconfdir}/default/btrfsmaintenance
}

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = " \
    btrfs-scrub.timer \
    btrfs-scrub.service \
    btrfs-trim.timer \
    btrfs-trim.service \
    btrfs-balance.timer \
    btrfs-balance.service \
    btrfs-defrag.timer \
    btrfs-defrag.service \
    btrfsmaintenance-refresh.service \
    btrfsmaintenance-refresh.path \
"
