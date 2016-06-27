SUMMARY = "Internationalized Domain Name support library"
DESCRIPTION = "Implementation of the Stringprep, Punycode and IDNA specifications defined by the IETF Internationalized Domain Names (IDN) working group."
HOMEPAGE = "http://www.gnu.org/software/libidn/"
SECTION = "libs"
LICENSE = "(LGPLv2.1+ | LGPLv3) & GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=df4be47940a91ee69556f5f71eed4aec \
                    file://COPYING.LESSERv2;md5=4fbd65380cdd255951079008b364516c \
                    file://COPYING.LESSERv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://lib/idna.h;endline=21;md5=7c0b3828d1b153663be9a04ad4f7975f \
                    file://src/idn.c;endline=20;md5=f4235f2a2cb2b65786b2979fb3cf7fbf"

inherit pkgconfig autotools gettext texinfo

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://libidn_fix_for_automake-1.12.patch \
           file://avoid_AM_PROG_MKDIR_P_warning_error_with_automake_1.12.patch \
           file://dont-depend-on-help2man.patch \
"

SRC_URI[md5sum] = "4dd8356ba577287ea7076bfa1554b534"
SRC_URI[sha256sum] = "ba5d5afee2beff703a34ee094668da5c6ea5afa38784cebba8924105e185c4f5"

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"
FILES_idn = "${bindir}/*"

EXTRA_OECONF = "--disable-csharp"

do_install_append() {
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"

