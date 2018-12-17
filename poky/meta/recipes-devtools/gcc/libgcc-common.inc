BPN = "libgcc"

require gcc-configure-common.inc

INHIBIT_DEFAULT_DEPS = "1"

do_configure () {
	install -d ${D}${base_libdir} ${D}${libdir}
	mkdir -p ${B}/${BPN}
	mkdir -p ${B}/${TARGET_SYS}/${BPN}/
	cd ${B}/${BPN}
	chmod a+x ${S}/${BPN}/configure
	relpath=${@os.path.relpath("${S}/${BPN}", "${B}/${BPN}")}
	$relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}
EXTRACONFFUNCS += "extract_stashed_builddir"
do_configure[depends] += "${COMPILERDEP}"

do_compile () {
	cd ${B}/${BPN}
	oe_runmake MULTIBUILDTOP=${B}/${TARGET_SYS}/${BPN}/
}

do_install () {
	cd ${B}/${BPN}
	oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/${TARGET_SYS}/${BPN}/ install

	# Move libgcc_s into /lib
	mkdir -p ${D}${base_libdir}
	if [ -f ${D}${libdir}/nof/libgcc_s.so ]; then
		mv ${D}${libdir}/nof/libgcc* ${D}${base_libdir}
	else
		mv ${D}${libdir}/libgcc* ${D}${base_libdir} || true
	fi

	# install the runtime in /usr/lib/ not in /usr/lib/gcc on target
	# so that cross-gcc can find it in the sysroot

	mv ${D}${libdir}/gcc/* ${D}${libdir}
	rm -rf ${D}${libdir}/gcc/
	# unwind.h is installed here which is shipped in gcc-cross
	# as well as target gcc and they are identical so we dont
	# ship one with libgcc here
	rm -rf ${D}${libdir}/${TARGET_SYS}/${BINV}/include
}

do_install_append_libc-baremetal () {
	rmdir ${D}${base_libdir}
}
do_install_append_libc-newlib () {
	rmdir ${D}${base_libdir}
}

# No rpm package is actually created but -dev depends on it, avoid dnf error
RDEPENDS_${PN}-dev_libc-baremetal = ""
RDEPENDS_${PN}-dev_libc-newlib = ""

BBCLASSEXTEND = "nativesdk"

addtask multilib_install after do_install before do_package do_populate_sysroot
# this makes multilib gcc files findable for target gcc
# e.g.
#    /usr/lib/i586-pokymllib32-linux/4.7/
# by creating this symlink to it
#    /usr/lib64/x86_64-poky-linux/4.7/32

fakeroot python do_multilib_install() {
    import re

    multilibs = d.getVar('MULTILIB_VARIANTS')
    if not multilibs or bb.data.inherits_class('nativesdk', d):
        return

    binv = d.getVar('BINV')

    mlprefix = d.getVar('MLPREFIX')
    if ('%slibgcc' % mlprefix) != d.getVar('PN'):
        return

    if mlprefix:
        orig_tune = d.getVar('DEFAULTTUNE_MULTILIB_ORIGINAL')
        orig_tune_params = get_tune_parameters(orig_tune, d)
        orig_tune_baselib = orig_tune_params['baselib']
        orig_tune_bitness = orig_tune_baselib.replace('lib', '')
        if not orig_tune_bitness:
            orig_tune_bitness = '32'

        src = '../../../' + orig_tune_baselib + '/' + \
            d.getVar('TARGET_SYS_MULTILIB_ORIGINAL') + '/' + binv + '/'

        dest = d.getVar('D') + d.getVar('libdir') + '/' + \
            d.getVar('TARGET_SYS') + '/' + binv + '/' + orig_tune_bitness

        if os.path.lexists(dest):
            os.unlink(dest)
        os.symlink(src, dest)
        return


    for ml in multilibs.split():
        tune = d.getVar('DEFAULTTUNE_virtclass-multilib-' + ml)
        if not tune:
            bb.warn('DEFAULTTUNE_virtclass-multilib-%s is not defined. Skipping...' % ml)
            continue

        tune_parameters = get_tune_parameters(tune, d)
        tune_baselib = tune_parameters['baselib']
        if not tune_baselib:
            bb.warn("Tune %s doesn't have a baselib set. Skipping..." % tune)
            continue

        tune_arch = tune_parameters['arch']
        tune_bitness = tune_baselib.replace('lib', '')
        if not tune_bitness:
            tune_bitness = '32' # /lib => 32bit lib

        tune_abiextension = tune_parameters['abiextension']
        if tune_abiextension:
            libcextension = '-gnu' + tune_abiextension
        else:
            libcextension = ''

        src = '../../../' + tune_baselib + '/' + \
            tune_arch + d.getVar('TARGET_VENDOR') + 'ml' + ml + \
            '-' + d.getVar('TARGET_OS') + libcextension +  '/' + binv + '/'

        dest = d.getVar('D') + d.getVar('libdir') + '/' + \
            d.getVar('TARGET_SYS') + '/' + binv + '/' + tune_bitness

        if os.path.lexists(dest):
            os.unlink(dest)
        os.symlink(src, dest)
}

def get_original_os(d):
    vendoros = d.expand('${TARGET_ARCH}${ORIG_TARGET_VENDOR}-${TARGET_OS}')
    for suffix in [d.getVar('ABIEXTENSION'), d.getVar('LIBCEXTENSION')]:
        if suffix and vendoros.endswith(suffix):
            vendoros = vendoros[:-len(suffix)]
    # Arm must use linux-gnueabi not linux as only the former is accepted by gcc
    if vendoros.startswith("arm-") and not vendoros.endswith("-gnueabi"):
        vendoros = vendoros + "-gnueabi"
    return vendoros

ORIG_TARGET_VENDOR := "${TARGET_VENDOR}"
BASETARGET_SYS = "${@get_original_os(d)}"

addtask extra_symlinks after do_multilib_install before do_package do_populate_sysroot
fakeroot python do_extra_symlinks() {
    if bb.data.inherits_class('nativesdk', d):
        return

    targetsys = d.getVar('BASETARGET_SYS')

    if targetsys != d.getVar('TARGET_SYS'):
        dest = d.getVar('D') + d.getVar('libdir') + '/' + targetsys
        src = d.getVar('TARGET_SYS')
        if not os.path.lexists(dest) and os.path.lexists(d.getVar('D') + d.getVar('libdir')):
            os.symlink(src, dest)
}
