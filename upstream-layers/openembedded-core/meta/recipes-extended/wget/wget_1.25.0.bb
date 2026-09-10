SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
DESCRIPTION = "Wget is a network utility to retrieve files from the web using \
HTTP(S) and FTP, the two most widely used internet protocols. It works \
non-interactively, so it will work in the background, after having logged off. \
The program supports recursive retrieval of web-authoring pages as well as \
FTP sites"
HOMEPAGE = "https://www.gnu.org/software/wget/"
SECTION = "console/network"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6f65012d1daf98cb09b386cfb68df26b"

inherit autotools gettext texinfo update-alternatives pkgconfig ptest

DEPENDS += "autoconf-archive-native pod2man-native"

SRC_URI = "${GNU_MIRROR}/wget/wget-${PV}.tar.gz \
           file://0002-improve-reproducibility.patch \
           file://CVE-2026-58469.patch \
           file://CVE-2026-58469-regression_p1.patch \
           file://CVE-2026-58469-regression_p2.patch \
           file://CVE-2026-58470.patch \
           file://CVE-2026-58470-regression.patch \
           file://CVE-2026-58472.patch \
           file://CVE-2026-58472-regression.patch \
           file://CVE-2026-58471.patch \
           file://CVE-2026-16599.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "766e48423e79359ea31e41db9e5c289675947a7fcf2efdcedb726ac9d0da3784"

EXTRA_OECONF = "--without-libgnutls-prefix --without-libssl-prefix \
                --disable-rpath --disable-ntlm --disable-pcre"

EXTRA_OEMAKE += 'TOOLCHAIN_OPTIONS="${TOOLCHAIN_OPTIONS}" \
                 DEBUG_PREFIX_MAP="${DEBUG_PREFIX_MAP}"'

PACKAGECONFIG ??= "gnutls pcre2 zlib libpsl \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ares] = "--with-cares,--without-cares,c-ares"
PACKAGECONFIG[gnutls] = "--with-ssl=gnutls,,gnutls"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[iri] = "--enable-iri,--disable-iri,libidn2"
PACKAGECONFIG[libpsl] = "--with-libpsl,--without-libpsl,libpsl"
PACKAGECONFIG[libuuid] = "--with-libuuid,--without-libuuid,util-linux"
PACKAGECONFIG[openssl] = "--with-ssl=openssl,,openssl"
PACKAGECONFIG[pcre2] = "--enable-pcre2,--disable-pcre2,libpcre2"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

ALTERNATIVE:${PN} = "wget"
ALTERNATIVE:${PN}:class-nativesdk = ""
ALTERNATIVE_PRIORITY = "100"

RRECOMMENDS:${PN} += "ca-certificates"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/testenv
    cp -r ${S}/testenv/certs ${D}${PTEST_PATH}/testenv/
    cp -r ${S}/testenv/conf ${D}${PTEST_PATH}/testenv/
    cp -r ${S}/testenv/exc ${D}${PTEST_PATH}/testenv/
    cp -r ${S}/testenv/misc ${D}${PTEST_PATH}/testenv/
    cp -r ${S}/testenv/server ${D}${PTEST_PATH}/testenv/
    cp -r ${S}/testenv/test ${D}${PTEST_PATH}/testenv/
    for t in ${S}/testenv/Test-*.py ${S}/testenv/test_*.py; do
        case $t in
            */Test-metalink-*) continue ;;
        esac
        install -m 0755 $t ${D}${PTEST_PATH}/testenv/
    done
}

RDEPENDS:${PN}-ptest += "python3-core python3-modules"

BBCLASSEXTEND = "nativesdk"
