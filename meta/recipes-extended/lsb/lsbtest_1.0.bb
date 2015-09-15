SUMMARY = "Automates Linux Standard Base (LSB) tests"
SECTION = "console/utils"
LICENSE = "GPLv2"
PR = "r3"

LIC_FILES_CHKSUM = "file://LSB_Test.sh;beginline=3;endline=16;md5=7063bb54b04719df0716b513447f4fc0"

SRC_URI = "file://LSB_Test.sh \
		   file://packages_list \
		   file://session \
		   "
RDEPENDS_${PN} = "rpm"

S = "${WORKDIR}"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/LSB_Test.sh ${D}${bindir}
	install -d  ${D}/opt/lsb-test
	install -m 0644 ${S}/packages_list ${D}/opt/lsb-test/packages_list
	install -m 0644 ${S}/session ${D}/opt/lsb-test/session
	if [ "${TARGET_ARCH}" = "i586" ] || [ "${TARGET_ARCH}" = "i686" ];then
		sed -i -e 's/lsbarch/ia32/g' -e 's/targetarch/i486/g' ${D}/opt/lsb-test/packages_list
		sed -i -e 's/targetarch/x86/g' ${D}/opt/lsb-test/session
	fi
	if [ "${TARGET_ARCH}" = "x86_64" ];then
		sed -i -e 's/lsbarch/amd64/g' -e 's/targetarch/x86_64/g' ${D}/opt/lsb-test/packages_list
		sed -i -e 's/targetarch/x86-64/g' ${D}/opt/lsb-test/session
	fi
	if [ "${TARGET_ARCH}" = "powerpc" ];then
		sed -i -e 's/lsbarch/ppc32/g' -e 's/targetarch/ppc/g' ${D}/opt/lsb-test/packages_list
		sed -i -e 's/targetarch/PPC32/g' ${D}/opt/lsb-test/session
	fi

	# For a ppc64 target. the default userspace is 32b.
	# Therefore, only change the lsbarch and targetarch
	# in the package_list when MLIB=lib64 is being used.
	# Otherwise, by default, the ppc32 LSB packages
	# will be downloaded by LSB_Test.sh
	if [ "${TARGET_ARCH}" = "powerpc64" ];then
		if [ "${PN}" != "${BPN}" ];then
			sed -i -e 's/lsbarch/ppc64/g' -e 's/targetarch/ppc64/g' ${D}/opt/lsb-test/packages_list
			sed -i -e 's/targetarch/PPC64/g' ${D}/opt/lsb-test/session
		fi
	fi
}

FILES_${PN} += "/opt/lsb-test/* \
               "
