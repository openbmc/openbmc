SUMMARY = "SGPIO captive backplane tool"
HOMEPAGE = "http://sources.redhat.com/lvm2/wiki/DMRAID_Eventing"
DESCRIPTION = "Intel SGPIO enclosure management utility"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BPN}-1.2-0.10-src.tar.gz/a417bf68da4e9bd79a4664c11d7debd1/${BPN}-1.2-0.10-src.tar.gz \
           file://Makefile-error-fix.patch \
           file://0001-makefile-Add-LDFLAGS-to-linking-rule.patch \
           "
SRC_URI[sha256sum] = "9bf8c42acaa247efd9321bdb1fc2390022f0c554d77fbbd4a7363d990fc0270b"

S = "${WORKDIR}/${BPN}"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE_GPL;md5=393a5ca445f6965873eca0259a17f833"

EXTRA_OEMAKE = "CFLAGS='${CFLAGS}'"
do_compile:prepend() {
    oe_runmake clean
}

do_install() {
    oe_runmake install \
        INSTALL="/usr/bin/install -p" \
        DESTDIR=${D} \
        SBIN_DIR=${D}/${sbindir} \
        MANDIR=${D}/${mandir}
}
