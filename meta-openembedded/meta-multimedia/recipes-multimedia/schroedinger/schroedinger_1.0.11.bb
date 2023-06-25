SUMMARY = "Dirac compressed video encoder/decoder"
HOMEPAGE = "http://schrodinger.sourceforge.net/"
LICENSE = "MPL-1.1 | GPL-2.0-only | LGPL-2.0-only | MIT"

LIC_FILES_CHKSUM = "file://COPYING;md5=d91a46405fc074b88c963cc4f2a0aae9 \
                    file://COPYING.GPL;md5=e181e3b7c66f5f96921d813c1074f833 \
                    file://COPYING.LGPL;md5=38c893e21baec4cd75ad800ba9e2410a \
                    file://COPYING.MIT;md5=8b345371b3536b4ce37ead1eafc88221 \
                    file://COPYING.MPL;md5=0117647fecb9a932c25a7bbfc0333c37"

DEPENDS = "orc-native orc"

SRC_URI = "https://download.videolan.org/contrib/${BPN}/${BP}.tar.gz \
           file://0001-testsuite-Add-tmp-orc.c-for-missing-_orc_code_orc_de.patch \
           file://configure.ac.patch"

SRC_URI[md5sum] = "da6af08e564ca1157348fb8d92efc891"
SRC_URI[sha256sum] = "1e572a0735b92aca5746c4528f9bebd35aa0ccf8619b22fa2756137a8cc9f912"

EXTRA_OECONF += "STAGING_DIR=${STAGING_DIR_NATIVE}"

inherit autotools-brokensep pkgconfig

