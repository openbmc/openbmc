SUMMARY = "mcelog daemon accounts memory and some other errors in various ways."
DESCRIPTION = "mcelog is required by both 32bit x86 Linux kernels (since 2.6.30) \
and 64bit Linux kernels (since early 2.6 kernel releases) to log machine checks \
and should run on all Linux systems that need error handling."
HOMEPAGE = "http://mcelog.org/"
SECTION = "System Environment/Base"

SRC_URI = "git://git.kernel.org/pub/scm/utils/cpu/mce/mcelog.git;protocol=http; \
    file://0001-test-avoid-the-pfa-test-hang.patch \
    file://run-ptest \
"

SRCREV = "391abaac9bdff385934b18134789bcc98b0adeb7"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;md5=74bb47b9a68850cb398665cf78b31de6"

S = "${WORKDIR}/git"

inherit autotools-brokensep ptest

COMPATIBLE_HOST = '(x86_64.*|i.86.*)-linux'

do_install_append() {
    install -d ${D}${sysconfdir}/cron.hourly
    install -m 0755 ${S}/mcelog.cron ${D}${sysconfdir}/cron.hourly/
    sed -i 's/bash/sh/' ${D}${sysconfdir}/cron.hourly/mcelog.cron
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}
    cp -r ${S}/tests ${S}/input ${D}${PTEST_PATH}
    sed -i 's#../../mcelog#mcelog#' ${D}${PTEST_PATH}/tests/test
}

RDEPENDS_${PN}-ptest += "make bash mce-inject"
