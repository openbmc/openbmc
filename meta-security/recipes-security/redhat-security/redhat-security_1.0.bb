SUMMARY = "redhat security tools"
DESCRIPTION = "Tools used by redhat linux distribution for security checks"
SECTION = "security"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "file://find-chroot-py.sh \
           file://find-chroot.sh \
           file://find-elf4tmp.sh \
           file://find-execstack.sh \
           file://find-hidden-exec.sh \
           file://find-nodrop-groups.sh \
           file://find-sh4errors.sh \
           file://find-sh4tmp.sh \
           file://lib-bin-check.sh \
           file://rpm-chksec.sh \
           file://rpm-drop-groups.sh \
           file://selinux-check-devices.sh \
           file://selinux-ls-unconfined.sh"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/find-chroot-py.sh    ${D}${bindir}
	install -m 0755 ${S}/find-chroot.sh    ${D}${bindir}
	install -m 0755 ${S}/find-elf4tmp.sh    ${D}${bindir}
	install -m 0755 ${S}/find-execstack.sh    ${D}${bindir}
	install -m 0755 ${S}/find-hidden-exec.sh    ${D}${bindir}
	install -m 0755 ${S}/find-nodrop-groups.sh    ${D}${bindir}
	install -m 0755 ${S}/find-sh4errors.sh    ${D}${bindir}
	install -m 0755 ${S}/find-sh4tmp.sh    ${D}${bindir}
	install -m 0755 ${S}/lib-bin-check.sh    ${D}${bindir}
	install -m 0755 ${S}/rpm-chksec.sh    ${D}${bindir}
	install -m 0755 ${S}/rpm-drop-groups.sh    ${D}${bindir}
	install -m 0755 ${S}/selinux-check-devices.sh    ${D}${bindir}
	install -m 0755 ${S}/selinux-ls-unconfined.sh    ${D}${bindir}
}

RDEPENDS:${PN} = "file libcap-ng procps findutils"
