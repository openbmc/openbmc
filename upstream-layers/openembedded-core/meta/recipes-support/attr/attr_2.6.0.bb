SUMMARY = "Utilities for manipulating filesystem extended attributes"
DESCRIPTION = "Implement the ability for a user to attach name:value pairs to objects within the XFS filesystem."

HOMEPAGE = "http://savannah.nongnu.org/projects/attr/"
SECTION = "libs"

DEPENDS = "virtual/libintl"

LICENSE = "GPL-2.0-or-later AND LGPL-2.1-or-later"
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:lib${BPN} = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=2d0aa14b3fce4694e4f615e30186335f \
                    file://doc/COPYING.LGPL;md5=b8d31f339300bc239d73461d68e77b9c \
                    file://tools/attr.c;endline=17;md5=be0403261f0847e5f43ed5b08d19593c \
                    file://libattr/libattr.c;endline=17;md5=7970f77049f8fa1199fff62a7ab724fb"

SRC_URI = "${SAVANNAH_GNU_MIRROR}/attr/${BP}.tar.gz \
           file://run-ptest \
"

SRC_URI[sha256sum] = "d42fa374513180bb48cb11a46696f488240e5124ff1e6ad88b0abff706985612"

inherit ptest update-alternatives autotools gettext

PACKAGES =+ "lib${BPN}"

FILES:lib${BPN} = "${libdir}/lib*${SOLIBS} ${sysconfdir}"

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "setfattr getfattr"
ALTERNATIVE_TARGET[setfattr] = "${bindir}/setfattr"
ALTERNATIVE_TARGET[getfattr] = "${bindir}/getfattr"

do_install_ptest() {
    install -m755 ${S}/test/run ${S}/test/sort-getfattr-output ${D}${PTEST_PATH}/

    for t in $(makefile-getvar ${B}/Makefile TESTS); do
        install -m644 ${S}/$t ${D}${PTEST_PATH}/
    done
}

do_install_ptest:append:libc-musl() {
    # With glibc strerror(ENOTSUP) is "Operation not supported" but
    # musl is "Not supported".
    # https://savannah.nongnu.org/bugs/?62370
    sed -i -e 's|f: Operation not supported|f: Not supported|g' ${D}${PTEST_PATH}/attr.run
}

RDEPENDS:${PN}-ptest = "attr \
                        perl \
                        perl-module-cwd \
                        perl-module-file-basename \
                        perl-module-file-path \
                        perl-module-filehandle \
                        perl-module-getopt-std \
                        perl-module-posix \
                        "

BBCLASSEXTEND = "native nativesdk"
