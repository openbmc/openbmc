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
                    file://lib/idna.h;endline=21;md5=37cffad24807f446a24de3e7371f20b9 \
                    file://src/idn.c;endline=20;md5=09e97034a8877b3451cb65065fc2c06e"
DEPENDS = "virtual/libiconv"

inherit pkgconfig autotools gettext texinfo gtk-doc

SRC_URI = "${GNU_MIRROR}/libidn/${BPN}-${PV}.tar.gz \
           file://libidn_fix_for_automake-1.12.patch \
           file://avoid_AM_PROG_MKDIR_P_warning_error_with_automake_1.12.patch \
           file://dont-depend-on-help2man.patch \
           file://0001-idn-fix-printf-format-security-warnings.patch \
           file://gcc7-compatibility.patch \
           file://0001-idn-format-security-warnings.patch \
"

SRC_URI[md5sum] = "a9aa7e003665de9c82bd3f9fc6ccf308"
SRC_URI[sha256sum] = "44a7aab635bb721ceef6beecc4d49dfd19478325e1b47f3196f7d2acc4930e19"

# command tool is under GPLv3+, while libidn itself is under LGPLv2.1+ or LGPLv3
# so package command into a separate package
PACKAGES =+ "idn"
FILES_idn = "${bindir}/*"

LICENSE_${PN} = "LGPLv2.1+ | LGPLv3"
LICENSE_idn = "GPLv3+"

EXTRA_OECONF = "--disable-csharp"

do_install_append() {
	rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"

