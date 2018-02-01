SECTION = "console/utils"
SUMMARY = "A free SOCKS server"
DESCRIPTION = "Dante consists of a SOCKS server and a SOCKS client,\
implementing RFC 1928 and related standards. It is a flexible product\
that can be used to provide convenient and secure network\
connectivity. Once installed, Dante can in most cases be made\
transparent to clients, providing functionality somewhat similar to\
what could be described as a non-transparent Layer 4 router."
HOMEPAGE = "http://www.inet.no/dante/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=221118dda731fe93a85d0ed973467249"

SRC_URI = "https://www.inet.no/dante/files/dante-${PV}.tar.gz \
          "
SRC_URI[md5sum] = "68c2ce12119e12cea11a90c7a80efa8f"
SRC_URI[sha256sum] = "b6d232bd6fefc87d14bf97e447e4fcdeef4b28b16b048d804b50b48f261c4f53"

# without --without-gssapi, config.log will contain reference to /usr/lib
# as a consequence of GSSAPI path being set to /usr by default.
# --with-gssapi-path=PATH specify gssapi path
# --without-gssapi        disable gssapi support
# --enable-release        build prerelease as full release
EXTRA_OECONF += "--without-gssapi --sbindir=${bindir}"

DEPENDS += "flex-native bison-native libpam"

inherit autotools-brokensep distro_features_check

REQUIRED_DISTRO_FEATURES = "pam"

EXTRA_AUTORECONF = "-I ${S}"

PACKAGECONFIG[libwrap] = ",--disable-libwrap,tcp-wrappers,libwrap"
PACKAGECONFIG[krb5] = ",--without-krb5,krb5"

PACKAGECONFIG ??= ""

do_install_append() {
    install -d ${D}${sysconfdir}
    cp ${S}/example/sock[sd].conf ${D}${sysconfdir}
}

PACKAGES =+ "${PN}-sockd ${PN}-libdsocks "

FILES_${PN}-libdsocks = "${libdir}/libdsocks.so"
FILES_${PN}-sockd = "${bindir}/sockd ${sysconfdir}/sockd.conf"

INSANE_SKIP_${PN}-libdsocks = "dev-elf"
