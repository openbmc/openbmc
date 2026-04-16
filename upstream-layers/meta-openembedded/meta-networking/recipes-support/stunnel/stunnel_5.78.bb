SUMMARY = "Program for providing universal TLS/SSL tunneling service"
DESCRIPTION = "SSL encryption wrapper between remote client and local (inetd-startable) or remote server."
HOMEPAGE = "https://www.stunnel.org/"
SECTION = "net"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.md;md5=df2a0f88c5d5e3ebce922682a7b598ec"

DEPENDS = "autoconf-archive libnsl2 openssl"

SRC_URI = "https://stunnel.org/archive/5.x/${BP}.tar.gz \
           file://fix-openssl-no-des.patch \
"

SRC_URI[sha256sum] = "8727e53bb8b7528f850327a2a149158422c02183bc120d1d733cc65b1e2c349d"

inherit autotools bash-completion pkgconfig

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)}"

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"

EXTRA_OECONF += "--with-ssl='${STAGING_EXECPREFIXDIR}' --disable-fips"

# When cross compiling, configure defaults to nobody, but provides no option to change it.
EXTRA_OEMAKE += "DEFAULT_GROUP='nogroup'"

# stunnel3 is a Perl wrapper to allow use of the legacy stunnel 3.x commandline
# syntax with stunnel >= 4.05
PACKAGES =+ "stunnel3"
FILES:stunnel3 = "${bindir}/stunnel3"
RDEPENDS:stunnel3 += "${PN} perl"
