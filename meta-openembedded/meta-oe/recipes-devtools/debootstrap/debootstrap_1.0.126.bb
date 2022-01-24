SUMMARY = "Install a Debian system into a subdirectory"
HOMEPAGE = "https://wiki.debian.org/Debootstrap"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=1e68ced6e1689d4cd9dac75ff5225608"

SRC_URI  = "\
    http://http.debian.net/debian/pool/main/d/debootstrap/debootstrap_${PV}.tar.gz \
    file://0001-support-to-override-usr-sbin-and-usr-share.patch \
    file://0002-support-to-override-usr-bin-arch-test.patch \
    file://0001-do-not-hardcode-the-full-path-of-dpkg.patch \
"

SRC_URI[sha256sum] = "bc48e1c500c33bed50bd00d201f338d3c92d6c0dcb1f202c3bc00ef01f96c618"

S = "${WORKDIR}/debootstrap"

DEPENDS = " \
    virtual/fakeroot-native \
"

fakeroot do_install() {
    oe_runmake 'DESTDIR=${D}' install
    chown -R root:root ${D}${datadir}/debootstrap
}

BBCLASSEXTEND = "native nativesdk"
