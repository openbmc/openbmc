SUMMARY = "Fetchmail retrieves mail from remote mail servers and forwards it via SMTP"
HOMEPAGE = "http://www.fetchmail.info/"
DESCRIPTION = "Fetchmail is a full-featured, robust, well-documented remote-mail retrieval and forwarding utility intended to be used over on-demand TCP/IP links (such as SLIP or PPP connections). It supports every remote-mail protocol now in use on the Internet: POP2, POP3, RPOP, APOP, KPOP, all flavors of IMAP, ETRN, and ODMR. It can even support IPv6 and IPSEC."
SECTION = "mail"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ca53985c1fd053ae0bffffaa89ed49f1"

DEPENDS = "openssl"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.xz \
           "
SRC_URI[md5sum] = "53d52179c8bf8e2f57489538b49bfda9"
SRC_URI[sha256sum] = "511b60daabf7543a01de06af07c8772290c6807cd53c42a8504960e978f3abea"

inherit autotools gettext python3-dir python3native

EXTRA_OECONF = "--with-ssl=${STAGING_DIR_HOST}${prefix}"

PACKAGES =+ "fetchmail-python"
FILES_fetchmail-python = "${libdir}/${PYTHON_DIR}/*"
