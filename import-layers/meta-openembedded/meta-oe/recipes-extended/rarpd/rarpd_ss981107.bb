SUMMARY = "The RARP daemon."
DESCRIPTION = "RARP (Reverse Address Resolution Protocol) is a protocol which \
allows individual devices on an IP network to get their own IP addresses from \
the RARP server. Some machines (e.g. SPARC boxes) use this protocol instead \
of e.g. DHCP to query their IP addresses during network bootup. \
Linux kernels up to 2.2 used to provide a kernel daemon for this service, \
but since 2.3 kernels it is served by this userland daemon. \
You should install rarpd if you want to set up a RARP server on your \
network."
SECTION = "System Environment/Daemons"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BP}.tar.gz/be2a88f8ccddf2a40ac484cb3294fedc/${BP}.tar.gz"
SRC_URI[md5sum] = "be2a88f8ccddf2a40ac484cb3294fedc"
SRC_URI[sha256sum] = "4d6145d435a5d8b567b9798620f57f9b0a464078a1deba267958f168fbe776e6"

SRC_URI += "file://0001-rarpd.8-add-man-file.patch \
    file://0002-Makefile-modify-compile-parameters.patch \
    file://0003-rarpd.c-bug-fix.patch \
    file://0004-rarpd.init-add-new-init-file.patch \
    file://0005-ethernet.c-remove-it.patch \
    file://ethers.sample \
    file://rarpd.service \
"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://rarpd.c;md5=199b20b172ea93121bc613a9c77b6931"

S = "${WORKDIR}/${BPN}"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man8
    install -m 755 rarpd.init ${D}${sysconfdir}/init.d/rarpd
    install -m 755 rarpd ${D}${sbindir}/rarpd
    install -m 644 rarpd.8 ${D}${mandir}/man8/rarpd.8
    install -m 644 ${WORKDIR}/ethers.sample ${D}${sysconfdir}/ethers

    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${WORKDIR}/rarpd.service ${D}${systemd_unitdir}/system/
    fi
}

inherit ${@bb.utils.filter('VIRTUAL-RUNTIME_init_manager', 'systemd', d)}

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "rarpd.service"
SYSTEMD_AUTO_ENABLE = "disable"

RDEPENDS_${PN} += "bash"
