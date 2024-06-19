SUMMARY = "Very high-quality data compression program"
DESCRIPTION = "bzip2 compresses files using the Burrows-Wheeler block-sorting text compression algorithm, and \
Huffman coding. Compression is generally considerably better than that achieved by more conventional \
LZ77/LZ78-based compressors, and approaches the performance of the PPM family of statistical compressors."
HOMEPAGE = "https://sourceware.org/bzip2/"
SECTION = "console/utils"
LICENSE = "bzip2-1.0.6 & GPL-3.0-or-later & Apache-2.0 & MS-PL & BSD-3-Clause & Zlib"
LICENSE:${PN} = "bzip2-1.0.6"
LICENSE:${PN}-dev = "bzip2-1.0.6"
LICENSE:${PN}-dbg = "bzip2-1.0.6"
LICENSE:${PN}-doc = "bzip2-1.0.6"
LICENSE:${PN}-src = "bzip2-1.0.6"
LICENSE:libbz2 = "bzip2-1.0.6"
LICENSE:${PN}-ptest = "bzip2-1.0.6 & GPL-3.0-or-later & Apache-2.0 & MS-PL & BSD-3-Clause & Zlib"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=4;endline=37;md5=600af43c50f1fcb82e32f19b32df4664 \
                    file://${UNPACKDIR}/git/commons-compress/LICENSE.txt;md5=86d3f3a95c324c9479bd8986968f4327 \
                    file://${UNPACKDIR}/git/dotnetzip/License.txt;md5=9cb56871eed4e748c3bc7e8ff352a54f \
                    file://${UNPACKDIR}/git/dotnetzip/License.zlib.txt;md5=cc421ccd22eeb2e5db6b79e6de0a029f \
                    file://${UNPACKDIR}/git/go/LICENSE;md5=5d4950ecb7b26d2c5e4e7b4e0dd74707 \
                    file://${UNPACKDIR}/git/lbzip2/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

SRC_URI = "https://sourceware.org/pub/${BPN}/${BPN}-${PV}.tar.gz \
           git://sourceware.org/git/bzip2-tests.git;name=bzip2-tests;branch=master;protocol=https \
           file://configure.ac;subdir=${BP} \
           file://Makefile.am;subdir=${BP} \
           file://run-ptest \
           "
SRC_URI[md5sum] = "67e051268d0c475ea773822f7500d0e5"
SRC_URI[sha256sum] = "ab5a03176ee106d3f0fa90e381da478ddae405918153cca248e682cd0c4a2269"

SRCREV_bzip2-tests = "f9061c030a25de5b6829e1abf373057309c734c0"

UPSTREAM_CHECK_URI = "https://www.sourceware.org/pub/bzip2/"

PACKAGES =+ "libbz2"

CFLAGS:append = " -fPIC -fpic -Winline -fno-strength-reduce -D_FILE_OFFSET_BITS=64"

inherit autotools update-alternatives ptest relative_symlinks

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "bunzip2 bzcat bzip2"

#install binaries to bzip2-native under sysroot for replacement-native
EXTRA_OECONF:append:class-native = " --bindir=${STAGING_BINDIR_NATIVE}/${PN}"

do_configure:prepend () {
    sed -i -e "s|%BZIP2_VERSION%|${PV}|" ${S}/configure.ac
}

do_install_ptest () {
	install -d ${D}${PTEST_PATH}/bzip2-tests
	cp -r ${UNPACKDIR}/git/commons-compress ${D}${PTEST_PATH}/bzip2-tests/commons-compress
	cp -r ${UNPACKDIR}/git/dotnetzip ${D}${PTEST_PATH}/bzip2-tests/dotnetzip
	cp -r ${UNPACKDIR}/git/go ${D}${PTEST_PATH}/bzip2-tests/go
	cp -r ${UNPACKDIR}/git/lbzip2 ${D}${PTEST_PATH}/bzip2-tests/lbzip2
	cp -r ${UNPACKDIR}/git/pyflate ${D}${PTEST_PATH}/bzip2-tests/pyflate
	cp ${UNPACKDIR}/git/README ${D}${PTEST_PATH}/bzip2-tests/
	cp ${UNPACKDIR}/git/run-tests.sh ${D}${PTEST_PATH}/bzip2-tests/
	sed -i -e "s|^Makefile:|_Makefile:|" ${D}${PTEST_PATH}/Makefile
}

FILES:libbz2 = "${libdir}/lib*${SOLIBS}"

RDEPENDS:${PN}-ptest += "make bash"

PROVIDES:append:class-native = " bzip2-replacement-native"
BBCLASSEXTEND = "native nativesdk"
