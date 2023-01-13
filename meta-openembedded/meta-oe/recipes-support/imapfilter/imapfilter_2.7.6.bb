SUMMARY = "IMAPFilter is a mail filtering utility that processes mailboxes based on IMAP queries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c11d4fd926d3ce7aac13b0ed1e9b3a63"

# v2.7.6
SRCREV = "b39d0430f29d7c953581186955c11b461e6c824f"
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
