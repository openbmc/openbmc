SUMMARY = "IMAPFilter is a mail filtering utility that processes mailboxes based on IMAP queries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f8d2fc4954306888fd0e4b27bef83525"

# v2.7.6
SRCREV = "9e6661278572009a92a8e125c9b339232a9735a1"
SRC_URI = "git://github.com/lefcha/imapfilter;protocol=https;branch=master \
           file://ldflags.patch \
"
S = "${WORKDIR}/git"

DEPENDS= "openssl lua libpcre2"

EXTRA_OEMAKE:append = " PREFIX=${prefix}"

do_install(){
    oe_runmake DESTDIR=${D} install

    # No need for manuals at this point, MANDIR is hardcoded to depend on prefix
    rm -rf ${D}${prefix}/man
}

ASNEEDED = ""
