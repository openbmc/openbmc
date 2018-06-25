SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
HOMEPAGE = "https://www.gnu.org/software/wget/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"
DEPENDS = "gnutls zlib libpcre"

inherit autotools gettext texinfo update-alternatives pkgconfig

EXTRA_OECONF = "--with-ssl=gnutls --disable-rpath --disable-iri \
                --without-libgnutls-prefix ac_cv_header_uuid_uuid_h=no"

EXTRA_OEMAKE += 'TOOLCHAIN_OPTIONS="${TOOLCHAIN_OPTIONS}" \
                 DEBUG_PREFIX_MAP="${DEBUG_PREFIX_MAP}"'

ALTERNATIVE_${PN} = "wget"
ALTERNATIVE_${PN}_class-nativesdk = ""
ALTERNATIVE_PRIORITY = "100"

RRECOMMENDS_${PN} += "ca-certificates"

BBCLASSEXTEND = "nativesdk"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[libuuid] = "--with-libuuid, --without-libuuid,util-linux"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# Let aclocal use the relative path for the m4 file rather than
# absolute, otherwise there might be an "Argument list too long" error
# when it is built in a long/deep directory.
acpaths = "-I ./m4"
