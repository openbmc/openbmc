SUMMARY = "FIDO 2.0 support library"
DESCRIPTION = "libfido2 provides library functionality and command-line tools to \
communicate with a FIDO device over USB, and to verify attestation and \
assertion signatures."
HOMEPAGE = "https://developers.yubico.com/libfido2"
LICENSE = "BSD-2-Clause"
SECTION = "libs/network"

LIC_FILES_CHKSUM = "file://LICENSE;md5=20be19aaa222f71738712b26f7f8717b"

SRC_URI = "https://developers.yubico.com/${BPN}/Releases/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "8c2b6fb279b5b42e9ac92ade71832e485852647b53607c43baaafbbcecea04e4"

inherit nopackages

PACKAGES = ""

# The purpose of this recipe is to break a circular dependency between libfido2 and
# systemd. libfido2 depends on udev (provided by systemd) to build, while systemd
# depends on libfido2 if the fido feature is enabled. However, systemd doesn't
# actually link against libfido2. It only needs the headers in place and a dummy
# shared library. It opportunistically dlopens libfido2 if it's present, but
# for that to work it needs the headers in place.
# Just fake enough to make systemd happy, and have it RRECOMMEND the real libfido2.
do_install() {
    mkdir -p ${D}${includedir}/libfido2-initial/fido
    mkdir -p ${D}${datadir}/pkgconfig

    install -m 644 ${S}/src/fido.h ${D}${includedir}/libfido2-initial
    install -m 644 ${S}/src/fido/* ${D}${includedir}/libfido2-initial/fido/

    # Real libfido2 installs its pkg conf file in ${libdir}.
    sed -e 's,@CMAKE_INSTALL_PREFIX@,${exec_prefix},' \
        -e 's,@CMAKE_INSTALL_LIBDIR@,${baselib}/libfido2-initial,' \
        -e 's,@FIDO_VERSION@,${PV},' \
        -e 's,@PROJECT_NAME@,${BPN},' \
        -e '/^Cflags/s,$,/libfido2-initial,' \
        ${S}/src/libfido2.pc.in > ${D}${datadir}/pkgconfig/libfido2.pc

}
