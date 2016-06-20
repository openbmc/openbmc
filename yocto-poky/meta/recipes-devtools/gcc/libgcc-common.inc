BPN = "libgcc"

require gcc-shared-source.inc

INHIBIT_DEFAULT_DEPS = "1"

do_configure () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	install -d ${D}${base_libdir} ${D}${libdir}
	hardlinkdir ${STAGING_INCDIR_NATIVE}/${LIBGCCBUILDTREENAME}$target/ ${B}
	mkdir -p ${B}/${BPN}
	mkdir -p ${B}/$target/${BPN}/
	cd ${B}/${BPN}
	chmod a+x ${S}/${BPN}/configure
	relpath=${@os.path.relpath("${S}/${BPN}", "${B}/${BPN}")}
	$relpath/configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

do_compile () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/${BPN}
	oe_runmake MULTIBUILDTOP=${B}/$target/${BPN}/
}

do_install () {
	target=`echo ${TARGET_SYS} | sed -e s#-${SDKPKGSUFFIX}##`
	cd ${B}/${BPN}
	oe_runmake 'DESTDIR=${D}' MULTIBUILDTOP=${B}/$target/${BPN}/ install

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

RDEPENDS_${PN}-dev_libc-baremetal = ""

BBCLASSEXTEND = "nativesdk"

addtask multilib_install after do_install before do_package do_populate_sysroot
# this makes multilib gcc files findable for target gcc
# e.g.
#    /usr/lib/i586-pokymllib32-linux/4.7/
# by creating this symlink to it
#    /usr/lib64/x86_64-poky-linux/4.7/32

fakeroot python do_multilib_install() {
    import re

    multilibs = d.getVar('MULTILIB_VARIANTS', True)
    if not multilibs or bb.data.inherits_class('nativesdk', d):
        return

    binv = d.getVar('BINV', True)

    mlprefix = d.getVar('MLPREFIX', True)
    if ('%slibgcc' % mlprefix) != d.getVar('PN', True):
        return

    if mlprefix:
        orig_tune = d.getVar('DEFAULTTUNE_MULTILIB_ORIGINAL', True)
        orig_tune_params = get_tune_parameters(orig_tune, d)
        orig_tune_baselib = orig_tune_params['baselib']
        orig_tune_bitness = orig_tune_baselib.replace('lib', '')
        if not orig_tune_bitness:
            orig_tune_bitness = '32'

        src = '../../../' + orig_tune_baselib + '/' + \
            d.getVar('TARGET_SYS_MULTILIB_ORIGINAL', True) + '/' + binv + '/'

        dest = d.getVar('D', True) + d.getVar('libdir', True) + '/' + \
            d.getVar('TARGET_SYS', True) + '/' + binv + '/' + orig_tune_bitness

        if os.path.lexists(dest):
            os.unlink(dest)
        os.symlink(src, dest)
        return


    for ml in multilibs.split():
        tune = d.getVar('DEFAULTTUNE_virtclass-multilib-' + ml, True)
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

        src = '../../../' + tune_baselib + '/' + \
            tune_arch + d.getVar('TARGET_VENDOR', True) + 'ml' + ml + \
            '-' + d.getVar('TARGET_OS', True) + '/' + binv + '/'

        dest = d.getVar('D', True) + d.getVar('libdir', True) + '/' + \
            d.getVar('TARGET_SYS', True) + '/' + binv + '/' + tune_bitness

        if os.path.lexists(dest):
            os.unlink(dest)
        os.symlink(src, dest)
}

def get_original_vendoros(d):
    vendoros = d.expand('${TARGET_VENDOR}-${TARGET_OS}')
    for suffix in [d.getVar('ABIEXTENSION', True), d.getVar('LIBCEXTENSION', True)]:
        if suffix and vendoros.endswith(suffix):
            vendoros = vendoros[:-len(suffix)]
    return vendoros

ORIG_TARGET_VENDOROS := "${@get_original_vendoros(d)}"
BASETARGET_SYS = "${TARGET_ARCH}${ORIG_TARGET_VENDOROS}"

addtask extra_symlinks after do_multilib_install before do_package do_populate_sysroot
fakeroot python do_extra_symlinks() {
    targetsys = d.getVar('BASETARGET_SYS', True)

    if targetsys != d.getVar('TARGET_SYS', True):
        dest = d.getVar('D', True) + d.getVar('libdir', True) + '/' + targetsys
        src = d.getVar('TARGET_SYS', True)
        if not os.path.lexists(dest) and os.path.lexists(d.getVar('D', True) + d.getVar('libdir', True)):
            os.symlink(src, dest)
}
