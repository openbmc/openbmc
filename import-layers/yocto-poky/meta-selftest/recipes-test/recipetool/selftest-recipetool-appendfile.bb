SUMMARY = "Test recipe for recipetool appendfile"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

SRC_URI = "file://installscript.sh \
           file://selftest-replaceme-orig \
           file://selftest-replaceme-todir \
           file://file1 \
           file://add-file.patch \
           file://subdir \
           file://selftest-replaceme-src-glob* \
           file://selftest-replaceme-inst-globfile \
           file://selftest-replaceme-inst-todir-globfile \
           file://selftest-replaceme-inst-func"

install_extrafunc() {
	install -m 0644 ${WORKDIR}/selftest-replaceme-inst-func ${D}${datadir}/selftest-replaceme-inst-func
}

do_install() {
	install -d ${D}${datadir}/
	install -m 0644 ${WORKDIR}/selftest-replaceme-orig ${D}${datadir}/selftest-replaceme-orig
	install -m 0644 ${WORKDIR}/selftest-replaceme-todir ${D}${datadir}
	install -m 0644 ${WORKDIR}/file1 ${D}${datadir}/selftest-replaceme-renamed
	install -m 0644 ${WORKDIR}/subdir/fileinsubdir ${D}${datadir}/selftest-replaceme-subdir
	install -m 0644 ${WORKDIR}/selftest-replaceme-src-globfile ${D}${datadir}/selftest-replaceme-src-globfile
	cp ${WORKDIR}/selftest-replaceme-inst-glob* ${D}${datadir}/selftest-replaceme-inst-globfile
	cp ${WORKDIR}/selftest-replaceme-inst-todir-glob* ${D}${datadir}
	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/file2 ${D}${sysconfdir}/selftest-replaceme-patched
	sh ${WORKDIR}/installscript.sh ${D}${datadir}
	install_extrafunc
}

pkg_postinst_${PN} () {
	echo "Test file installed by postinst" > $D${datadir}/selftest-replaceme-postinst
}

FILES_${PN} += "${datadir}"

