SECTION = "base"
SUMMARY = "MIME files 'mime.types' & 'mailcap', and support programs"
LICENSE = "PD"
LIC_FILES_CHKSUM = "file://debian/README;md5=36bbe2ace0a05c3fb684b73208fbf30b"

DEPENDS = "file"
RDEPENDS_${PN} = "perl"
RRECOMMENDS_${PN} = "file"

SRC_URI = "${DEBIAN_MIRROR}/main/m/mime-support/mime-support_${PV}-1.tar.gz"
S = "${WORKDIR}/${BPN}"

inherit update-alternatives

FILES_${PN} += " ${libdir}/mime"

docdir_append = "/${BPN}"

do_install () {
    install -d ${D}${sysconfdir}
    install -d ${D}${libdir}/mime/packages
    install -d ${D}${docdir}
    install -d ${D}${sbindir}
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${mandir}/man5
    install -d ${D}${mandir}/man8
    install -m 644 mime.types         ${D}${sysconfdir}/
    install -m 644 mailcap            ${D}${libdir}/mime/
    install -m 644 mailcap.order           ${D}${sysconfdir}/
    install -m 644 mailcap.man        ${D}${mandir}/man5/mailcap.5
    install -m 644 mailcap.order.man    ${D}${mandir}/man5/mailcap.order.5
#    install -m 755 install-mime        ${D}${sbindir}/
#    install -m 644 install-mime.man        ${D}${mandir}/man8/install-mime.8
    install -m 755 update-mime        ${D}${sbindir}/
    install -m 644 update-mime.man        ${D}${mandir}/man8/update-mime.8
    install -m 755 run-mailcap        ${D}${bindir}/
    install -m 644 run-mailcap.man        ${D}${mandir}/man1/run-mailcap.1
#    install -m 644 rfcs/*            ${D}${docdir}/
    install -m 644 debian/changelog        ${D}${docdir}/changelog.Debian
    install -m 644 debian/README        ${D}${docdir}/copyright
    install -m 755 debian-view        ${D}${libdir}/mime/
    install -m 755 playaudio        ${D}${libdir}/mime/
    install -m 755 playdsp            ${D}${libdir}/mime/
    install -m 644 mailcap.entries        ${D}${libdir}/mime/packages/mime-support
    cd ${D}${mandir}; gzip -9fv */*
    cd ${D}${docdir}; gzip -9v *
    cd ${D}${docdir}; gunzip copyright.gz
    cd ${D}${bindir}; ln -s run-mailcap see
    cd ${D}${bindir}; ln -s run-mailcap edit
    cd ${D}${bindir}; ln -s run-mailcap compose
    cd ${D}${bindir}; ln -s run-mailcap print
    cd ${D}${mandir}/man1; ln -s run-mailcap.1.gz see.1.gz
    cd ${D}${mandir}/man1; ln -s run-mailcap.1.gz edit.1.gz
    cd ${D}${mandir}/man1; ln -s run-mailcap.1.gz compose.1.gz
    cd ${D}${mandir}/man1; ln -s run-mailcap.1.gz print.1.gz
}

SRC_URI[md5sum] = "d6e5d715e331147352c50c158dbdec6d"
SRC_URI[sha256sum] = "a529c7892cb786f514af71f4ca5a4c3ebc58b538a49ff959c0d97592d38f040a"

ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE_${PN} = "mime.types"
ALTERNATIVE_LINK_NAME[mime.types] = "${sysconfdir}/mime.types"
