SUMMARY = "Fetchmail retrieves mail from remote mail servers and forwards it via SMTP"
HOMEPAGE = "http://www.fetchmail.info/"
DESCRIPTION = "Fetchmail is a full-featured, robust, well-documented remote-mail retrieval \
and forwarding utility intended to be used over on-demand TCP/IP links (such as SLIP or PPP \
connections). It supports every remote-mail protocol now in use on the Internet: POP2, POP3, \
RPOP, APOP, KPOP, all flavors of IMAP, ETRN, and ODMR. It can even support IPv6 and IPSEC."
SECTION = "mail"
LICENSE = "GPL-2.0-only & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c3a05d9b9d3784c824c9b92a648e1353"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz \
           "
SRC_URI[sha256sum] = "5f7a5e13731431134a2ca535bbced7adc666d3aeb93169a0830945d91f492300"

inherit autotools gettext pkgconfig python3-dir python3native

EXTRA_OECONF = "--with-ssl=${STAGING_DIR_HOST}${prefix}"

PACKAGES =+ "fetchmail-python"
FILES:fetchmail-python = "${libdir}/${PYTHON_DIR}/*"
