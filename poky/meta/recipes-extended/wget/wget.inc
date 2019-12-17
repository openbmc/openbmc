SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
DESCRIPTION = "Wget is a network utility to retrieve files from the web using \
HTTP(S) and FTP, the two most widely used internet protocols. It works \
non-interactively, so it will work in the background, after having logged off. \
The program supports recursive retrieval of web-authoring pages as well as \
FTP sites"
HOMEPAGE = "https://www.gnu.org/software/wget/"
SECTION = "console/network"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=c678957b0c8e964aa6c70fd77641a71e"

inherit autotools gettext texinfo update-alternatives pkgconfig

EXTRA_OECONF = "--without-libgnutls-prefix --without-libssl-prefix \
                --disable-rpath"

EXTRA_OEMAKE += 'TOOLCHAIN_OPTIONS="${TOOLCHAIN_OPTIONS}" \
                 DEBUG_PREFIX_MAP="${DEBUG_PREFIX_MAP}"'

ALTERNATIVE_${PN} = "wget"
ALTERNATIVE_${PN}_class-nativesdk = ""
ALTERNATIVE_PRIORITY = "100"

RRECOMMENDS_${PN} += "ca-certificates"

BBCLASSEXTEND = "nativesdk"

PACKAGECONFIG ??= "gnutls pcre zlib \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ares] = "--with-cares,--without-cares,c-ares"
PACKAGECONFIG[gnutls] = "--with-ssl=gnutls,,gnutls"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[iri] = "--enable-iri,--disable-iri,libidn2"
PACKAGECONFIG[libpsl] = "--with-libpsl,--without-libpsl,libpsl"
PACKAGECONFIG[libuuid] = "--with-libuuid,--without-libuuid,util-linux"
PACKAGECONFIG[openssl] = "--with-ssl=openssl,,openssl"
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

# Let aclocal use the relative path for the m4 file rather than
# absolute, otherwise there might be an "Argument list too long" error
# when it is built in a long/deep directory.
acpaths = "-I ./m4"
