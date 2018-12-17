SUMMARY = "Program for providing universal TLS/SSL tunneling service"
DESCRIPTION = "SSL encryption wrapper between remote client and local (inetd-startable) or remote server."
HOMEPAGE = "https://www.stunnel.org/"
SECTION = "net"
# Note: Linking stunnel statically or dynamically with other modules is making
# a combined work based on stunnel. Thus, the terms and conditions of the GNU
# General Public License cover the whole combination.
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f6b7fe7379c9c2d7db6c80f7bd41e06d"

DEPENDS = "autoconf-archive libnsl2 openssl"

SRC_URI = "ftp://ftp.stunnel.org/stunnel/archive/5.x/${BP}.tar.gz \
           file://fix-openssl-no-des.patch \
"

SRC_URI[md5sum] = "0b41240e5585ec7d55ca343feed5530f"
SRC_URI[sha256sum] = "3d6641213a82175c19f23fde1c3d1c841738385289eb7ca1554f4a58b96d955e"

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
