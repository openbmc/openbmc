SUMMARY = "redhat security tools"
DESCRIPTION = "Tools used by redhat linux distribution for security checks"
SECTION = "security"
LICENSE = "GPLv2"
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

S = "${WORKDIR}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-chroot-py.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-chroot.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-elf4tmp.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-execstack.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-hidden-exec.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-nodrop-groups.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-sh4errors.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/find-sh4tmp.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/lib-bin-check.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/rpm-chksec.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/rpm-drop-groups.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/selinux-check-devices.sh    ${D}${bindir}
	install -m 0755 ${WORKDIR}/selinux-ls-unconfined.sh    ${D}${bindir}
}

RDEPENDS_${PN} = "file libcap-ng procps findutils"
