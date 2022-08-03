HOMEPAGE =  "https://github.com/open-power/pdata/"

SUMMARY     = "POWER Host data management"
DESCRIPTION = "Devicetree based POWER host data management"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

S = "${WORKDIR}/git"

inherit autotools \
        perlnative

SRC_URI = "git://git@github.com/open-power/pdata;branch="main";protocol=https"
SRCREV = "1ae8abf6c3caa4a166b73ef15512cdf4e78d4b5f"

DEPENDS = "pdbg \
           libxml-simple-perl-native \
           libxml-libxml-perl-native \
           ekb-native \
           autoconf-archive \
           dtc-native"

PDATA_CONFARG = "CHIP=p10"
EXTRA_OECONF = "--enable-gen_attrsinfo ${PDATA_CONFARG}"

EXTRA_OEMAKE = "EKB=${STAGING_DATADIR_NATIVE}/ekb/"
