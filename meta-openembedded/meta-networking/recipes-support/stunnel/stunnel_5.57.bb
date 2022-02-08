SUMMARY = "Program for providing universal TLS/SSL tunneling service"
DESCRIPTION = "SSL encryption wrapper between remote client and local (inetd-startable) or remote server."
HOMEPAGE = "https://www.stunnel.org/"
SECTION = "net"
# Note: Linking stunnel statically or dynamically with other modules is making
# a combined work based on stunnel. Thus, the terms and conditions of the GNU
# General Public License cover the whole combination.
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=6bae28875b3b599f8f621f4335b17955"

DEPENDS = "autoconf-archive libnsl2 openssl"

SRC_URI = "ftp://ftp.stunnel.org/stunnel/archive/5.x/${BP}.tar.gz \
           file://fix-openssl-no-des.patch \
"

SRC_URI[sha256sum] = "af5ab973dde11807c38735b87bdd87563a47d2fa1c72a07929fcfce80a600fe1"

inherit autotools

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)} libwrap"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[libwrap] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

EXTRA_OECONF += "--with-ssl='${STAGING_EXECPREFIXDIR}' --disable-fips"

# When cross compiling, configure defaults to nobody, but provides no option to change it.
EXTRA_OEMAKE += "DEFAULT_GROUP='nogroup'"

# stunnel3 is a Perl wrapper to allow use of the legacy stunnel 3.x commandline
# syntax with stunnel >= 4.05
PACKAGES =+ "stunnel3"
FILES_stunnel3 = "${bindir}/stunnel3"
RDEPENDS_stunnel3 += "${PN} perl"
