SUMMARY = "mcelog daemon accounts memory and some other errors in various ways."
DESCRIPTION = "mcelog is required by both 32bit x86 Linux kernels (since 2.6.30) \
and 64bit Linux kernels (since early 2.6 kernel releases) to log machine checks \
and should run on all Linux systems that need error handling."
HOMEPAGE = "https://mcelog.org/"
SECTION = "System Environment/Base"

SRC_URI = "\
    git://git.kernel.org/pub/scm/utils/cpu/mce/mcelog.git;protocol=http;;branch=master \
    file://run-ptest \
    file://0001-genconfig.py-update-shebang.patch \
"

SRCREV = "4146c9296a0cbd26f1c5e411cb44877f350053bd"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"

inherit autotools-brokensep ptest

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'

do_install:append() {
    install -d ${D}${sysconfdir}/cron.hourly
    install -m 0755 ${S}/mcelog.cron ${D}${sysconfdir}/cron.hourly/
    sed -i 's/bash/sh/' ${D}${sysconfdir}/cron.hourly/mcelog.cron
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${S}/tests ${S}/input ${D}${PTEST_PATH}
    sed -i 's#../../mcelog#mcelog#' ${D}${PTEST_PATH}/tests/test
}

RDEPENDS:${PN}-ptest += "make bash mce-inject"
