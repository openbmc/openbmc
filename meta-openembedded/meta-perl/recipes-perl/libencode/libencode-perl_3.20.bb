# NOTE:
#    You should use perl-module-encode rather than this package
#    unless you specifically need a version newer than what is
#    provided by perl.

SUMMARY = "Encode - character encodings"
DESCRIPTION = "The \"Encode\" module provides the interfaces between \
Perl's strings and the rest of the system.  Perl strings are sequences \
of characters."

HOMEPAGE = "https://metacpan.org/release/Encode"
SECTION = "lib"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.json;beginline=8;endline=10;md5=b12e3be1e17a7e99ca4f429ff32c28b5"

SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DANKOGAI/Encode-${PV}.tar.gz"
SRC_URI[sha256sum] = "ced8fc06c54c05f227bfd9db38c1ed48a5a295db89f497d42b335afbf1f73517"

UPSTREAM_CHECK_REGEX = "Encode\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Encode-${PV}"

#EXTRA_CPANFLAGS:append = "PERL=../recipe-sysroot-native/usr/bin/perl-native/perl"
inherit cpan ptest-perl
do_install:prepend() {
    # Requires "-T" (taint) option on command line
    rm -rf ${B}/t/taint.t
    # Circular dependency of perl-module-open on perl-module-encode
    # and we cannot load perl-module-encode because we are providing
    # an alternative
    rm -rf ${B}/t/use-Encode-Alias.t
}

do_install:append() {
    sed -i -e "s|${STAGING_BINDIR_NATIVE}/perl-native||g" ${S}/*/*.exh ${S}/*.exh
}

do_install_ptest() {
    mkdir ${D}${PTEST_PATH}/bin
    cp -r ${B}/bin/piconv ${D}${PTEST_PATH}/bin
    cp -r ${B}/blib ${D}${PTEST_PATH}
    chown -R root:root ${D}${PTEST_PATH}
}

#  file /usr/bin/enc2xs from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
#  file /usr/bin/encguess from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
#  file /usr/bin/piconv from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
RCONFLICTS:${PN} = "perl-misc perl-module-encode"

RDEPENDS:${PN} += " \
    perl-module-bytes \
    perl-module-constant \
    perl-module-parent \
    perl-module-storable \
    perl-module-xsloader \
    "

RPROVIDES:${PN} += " \
    libencode-alias-perl \
    libencode-byte-perl \
    libencode-cjkconstants-perl \
    libencode-cn-perl \
    libencode-cn-hz-perl \
    libencode-config-perl \
    libencode-ebcdic-perl \
    libencode-encoder-perl \
    libencode-encoding-perl \
    libencode-gsm0338-perl \
    libencode-guess-perl \
    libencode-jp-perl \
    libencode-jp-h2z-perl \
    libencode-jp-jis7-perl \
    libencode-kr-perl \
    libencode-kr-2022_kr-perl \
    libencode-mime-header-perl \
    libencode-mime-name-perl \
    libencode-symbol-perl \
    libencode-tw-perl \
    libencode-unicode-perl \
    libencode-unicode-utf7-perl \
    libencoding-perl \
    libencode-internal-perl \
    libencode-mime-header-iso_2022_jp-perl \
    libencode-utf8-perl \
    libencode-utf_ebcdic-perl \
    "

RDEPENDS:${PN}-ptest += " \
    perl-module-blib \
    perl-module-charnames \
    perl-module-file-compare \
    perl-module-file-copy \
    perl-module-filehandle \
    perl-module-findbin \
    perl-module-integer \
    perl-module-io-select \
    perl-module-ipc-open3 \
    perl-module-mime-base64 \
    perl-module-perlio \
    perl-module-perlio-encoding \
    perl-module-perlio-scalar \
    perl-module-test-more \
    perl-module-tie-scalar \
    perl-module-unicore \
    perl-module-utf8 \
    "

BBCLASSEXTEND = "native"
