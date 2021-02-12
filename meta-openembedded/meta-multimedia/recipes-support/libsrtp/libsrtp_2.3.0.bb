DESCRIPTION = "library implementing Secure RTP (RFC 3711)"
HOMEPAGE = "https://github.com/cisco/libsrtp"
SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2909fcf6f09ffff8430463d91c08c4e1"

S = "${WORKDIR}/git"
SRCREV = "d02d21111e379c297e93a9033d7b653135f732ee"
SRC_URI = "git://github.com/cisco/libsrtp.git"

inherit autotools pkgconfig

EXTRA_OEMAKE += "shared_library"

do_configure_prepend() {
    cp ${STAGING_DATADIR_NATIVE}/automake-*/ar-lib ${S}
}

ALLOW_EMPTY_${PN} = "1"
