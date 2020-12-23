SUMMARY = "GNU cpio is a program to manage archives of files"
DESCRIPTION = "GNU cpio is a tool for creating and extracting archives, or copying files from one place to \
another. It handles a number of cpio formats as well as reading and writing tar files."
HOMEPAGE = "http://www.gnu.org/software/cpio/"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "${GNU_MIRROR}/cpio/cpio-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0002-src-global.c-Remove-superfluous-declaration-of-progr.patch \
           "

SRC_URI[md5sum] = "389c5452d667c23b5eceb206f5000810"
SRC_URI[sha256sum] = "e87470d9c984317f658567c03bfefb6b0c829ff17dbf6b0de48d71a4c8f3db88"

inherit autotools gettext texinfo

EXTRA_OECONF += "DEFAULT_RMT_DIR=${sbindir}"

do_install () {
    autotools_do_install
    if [ "${base_bindir}" != "${bindir}" ]; then
        install -d ${D}${base_bindir}/
        mv "${D}${bindir}/cpio" "${D}${base_bindir}/cpio"
        if [ "${sbindir}" != "${bindir}" ]; then
            rmdir ${D}${bindir}/
        fi
    fi

    # Avoid conflicts with the version from tar
    mv "${D}${mandir}/man8/rmt.8" "${D}${mandir}/man8/rmt-cpio.8"
}

PACKAGES =+ "${PN}-rmt"

FILES_${PN}-rmt = "${sbindir}/rmt*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN} = "cpio"
ALTERNATIVE_${PN}-rmt = "rmt"

ALTERNATIVE_LINK_NAME[cpio] = "${base_bindir}/cpio"

ALTERNATIVE_PRIORITY[rmt] = "50"
ALTERNATIVE_LINK_NAME[rmt] = "${sbindir}/rmt"

BBCLASSEXTEND = "native nativesdk"
