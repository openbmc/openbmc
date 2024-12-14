require glibc_${PV}.bb
require glibc-tests.inc

inherit ptest features_check
REQUIRED_DISTRO_FEATURES = "ptest"

SRC_URI += "\
	file://run-ptest \
"

SUMMARY = "glibc tests using time32/time64 interfaces to be run with ptest for the purpose of checking y2038 compatiblity"

# Erase some variables already set by glibc_${PV}
python __anonymous() {
       # Remove packages provided by glibc build, we only need a subset of them
       d.setVar("PACKAGES", "${PN} ${PN}-dbg ${PN}-ptest")

       d.setVar("PROVIDES", "${PN} ${PN}-ptest")

       bbclassextend = d.getVar("BBCLASSEXTEND").replace("nativesdk", "").strip()
       d.setVar("BBCLASSEXTEND", bbclassextend)
       d.setVar("RRECOMMENDS", "")
       d.setVar("SYSTEMD_SERVICE:nscd", "")
       d.setVar("SYSTEMD_PACKAGES", "")
}

# Remove any leftovers from original glibc recipe
RPROVIDES:${PN} = "${PN}"
RRECOMMENDS:${PN} = ""
RDEPENDS:${PN} = "glibc libgcc sed bash"
RDEPENDS:${PN}-ptest = "${PN}"
DEPENDS += "sed"

export oe_srcdir="${exec_prefix}/src/debug/glibc/${PV}/"

# Just build tests for target - do not run them
do_check:append () {
	oe_runmake -i check run-built-tests=no
}
addtask do_check after do_compile before do_install_ptest_base

glibc_strip_build_directory () {
	# Delete all non executable files from build directory
	find ${B} ! -executable -type f -delete

	# Remove build dynamic libraries and links to them as
	# those are already installed in the target device
	find ${B} -type f -name "*.so" -delete
	find ${B} -type l -name "*.so*" -delete

	# Remove headers (installed with glibc)
	find ${B} -type f -name "*.h" -delete

	find ${B} -type f -name "isomac" -delete
	find ${B} -type f -name "annexc" -delete
}

do_install_ptest_base () {
	glibc_strip_build_directory

	ls -r ${B}/*/*-time64 > ${B}/tst_time64

	# Remove '-time64' suffix - those tests are also time related
	sed -e "s/-time64$//" ${B}/tst_time64 > ${B}/tst_time_tmp
	tst_time=$(cat ${B}/tst_time_tmp ${B}/tst_time64)

	rm ${B}/tst_time_tmp ${B}/tst_time64
	echo "${tst_time}"

	# Install build test programs to the image
	install -d ${D}${PTEST_PATH}/tests/glibc-ptest/

	for f in "${tst_time}"
	do
	    cp -r ${f} ${D}${PTEST_PATH}/tests/glibc-ptest/
	done

	install -d ${D}${PTEST_PATH}
	cp ${UNPACKDIR}/run-ptest ${D}${PTEST_PATH}/

}

# The datadir directory is required to allow core (and reused)
# glibc cleanup function to finish correctly, as this directory
# is not created for ptests
stash_locale_package_cleanup:prepend () {
	mkdir -p ${PKGD}${datadir}
}

stash_locale_sysroot_cleanup:prepend () {
	mkdir -p ${SYSROOT_DESTDIR}${datadir}
}

# Prevent the do_package() task to set 'libc6' prefix
# for glibc tests related packages
python populate_packages:prepend () {
    if d.getVar('DEBIAN_NAMES'):
        d.setVar('DEBIAN_NAMES', '')
}

FILES:${PN} = "${PTEST_PATH}/* /usr/src/debug/${PN}/*"

EXCLUDE_FROM_SHLIBS = "1"

deltask do_stash_locale
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
