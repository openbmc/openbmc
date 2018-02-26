SUMMARY = "Fetchmail retrieves mail from remote mail servers and forwards it via SMTP"
HOMEPAGE = "http://www.fetchmail.info/"
DESCRIPTION = "Fetchmail is a full-featured, robust, well-documented remote-mail retrieval and forwarding utility intended to be used over on-demand TCP/IP links (such as SLIP or PPP connections). It supports every remote-mail protocol now in use on the Internet: POP2, POP3, RPOP, APOP, KPOP, all flavors of IMAP, ETRN, and ODMR. It can even support IPv6 and IPSEC."
SECTION = "mail"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbb509e0303f5ded1cbfc0cc8705f28c"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "61b66faad044afa26e142bb1791aa2b3"
SRC_URI[sha256sum] = "79b4c54cdbaf02c1a9a691d9948fcb1a77a1591a813e904283a8b614b757e850"

inherit autotools gettext python-dir pythonnative

EXTRA_OECONF = "--with-ssl=${STAGING_DIR_HOST}${prefix}"

PACKAGES =+ "fetchmail-python"
FILES_fetchmail-python = "${libdir}/${PYTHON_DIR}/*"
