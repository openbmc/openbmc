SUMMARY = "Install a Debian system into a subdirectory"
HOMEPAGE = "https://wiki.debian.org/Debootstrap"
SECTION = "devel"
LICENSE = "debootstrap-custom-license"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=1e68ced6e1689d4cd9dac75ff5225608"

inherit pkgconfig

SRC_URI  = "\
    http://http.debian.net/debian/pool/main/d/debootstrap/debootstrap_1.0.67.tar.gz \
    file://devices.tar.gz;unpack=0 \
"

SRC_URI[md5sum] = "eacabfe2e45415af60b1d74c3a23418a"
SRC_URI[sha256sum] = "0a12e0a2bbff185d47711a716b1f2734856100e8784361203e834fed0cffa51b"

S = "${WORKDIR}/${BP}"

# All Makefile does is creation of devices.tar.gz, which fails in OE build, we use
# static devices.tar.gz as work around
# | NOTE: make -j 8 -e MAKEFLAGS=
# | rm -rf dev
# | mkdir -p dev
# | chown 0:0 dev
# | chown: changing ownership of `dev': Operation not permitted
# | make: *** [devices.tar.gz] Error 1
# | WARNING: exit code 1 from a shell command.
do_compile_prepend() {
    cp ${WORKDIR}/devices.tar.gz ${B}
}

do_install() {
    oe_runmake 'DESTDIR=${D}' install
    chown -R root:root ${D}${datadir}/debootstrap
}
