SUMMARY = "Build Result Compare Script"
DESCRIPTION = "This package contains scripts to find out if the build result\
differs to a former build."
HOMEPAGE = "https://github.com/openSUSE/build-compare"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://github.com/openSUSE/build-compare.git \
           file://Rename-rpm-check.sh-to-pkg-diff.sh.patch;striplevel=1 \
           file://Ignore-DWARF-sections.patch;striplevel=1 \
           file://0001-Add-support-for-deb-and-ipk-packaging.patch \
           file://functions.sh-remove-space-at-head.patch \
           file://functions.sh-run-rpm-once-to-make-it-faster.patch \
           file://pkg-diff.sh-check-for-fifo-named-pipe.patch \
           file://pkg-diff.sh-check_single_file-return-at-once-when-sa.patch \
           file://pkg-diff.sh-remove-space-in-the-end-for-ftype.patch \
           file://functions.sh-improve-deb-and-ipk-checking.patch \
           "

# Date matches entry in build-compare.changes and date of SRCREV.
#
SRCREV = "c5352c054c6ef15735da31b76d6d88620f4aff0a"
PE = "1"
PV = "2015.02.10+git${SRCPV}"
UPSTREAM_VERSION_UNKNOWN = "1"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"

do_install() {
    install -d ${D}/${bindir}
    install -m 755 functions.sh ${D}/${bindir}
    install -m 755 pkg-diff.sh ${D}/${bindir}
    install -m 755 same-build-result.sh ${D}/${bindir}
    install -m 755 srpm-check.sh ${D}/${bindir}
}

RDEPENDS_${PN} += "bash"
