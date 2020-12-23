SUMMARY = "IMAPFilter is a mail filtering utility that processes mailboxes based on IMAP queries"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ccca8573ead8e965c130b6b2946a36ab"

SRC_URI = "https://codeload.github.com/lefcha/${BPN}/tar.gz/v${PV};downloadfilename=${BP}.tar.gz \
           file://ldflags.patch \
"
SRC_URI[sha256sum] = "90af9bc9875e03fb5a09a3233287b74dd817867cb18ec9ff52fead615755563e"

DEPENDS= "openssl lua libpcre"

EXTRA_OEMAKE_append = " PREFIX=${prefix}"

do_install(){
    oe_runmake DESTDIR=${D} install

    # No need for manuals at this point, MANDIR is hardcoded to depend on prefix
    rm -rf ${D}${prefix}/man
}

ASNEEDED = ""
