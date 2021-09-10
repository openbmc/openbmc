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

SRC_URI[md5sum] = "8a5e97f60236c3a63f715c056a5f0e29"
SRC_URI[sha256sum] = "7700e9864b29f166b26c54153ed8c275b46f4b9a60468bb607e019a64c16cd8f"

S = "${WORKDIR}/debootstrap"

DEPENDS = " \
    virtual/fakeroot-native \
"

fakeroot do_install() {
    oe_runmake 'DESTDIR=${D}' install
    chown -R root:root ${D}${datadir}/debootstrap
}

BBCLASSEXTEND = "native nativesdk"
