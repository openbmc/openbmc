inherit chrpath

SYSROOT_PREPROCESS_FUNCS += "relocatable_binaries_preprocess relocatable_native_pcfiles"

python relocatable_binaries_preprocess() {
    rpath_replace(d.expand('${SYSROOT_DESTDIR}'), d)
}

relocatable_native_pcfiles () {
	if [ -d ${SYSROOT_DESTDIR}${libdir}/pkgconfig ]; then
		rel=${@os.path.relpath(d.getVar('base_prefix'), d.getVar('libdir') + "/pkgconfig")}
		sed -i -e "s:${base_prefix}:\${pcfiledir}/$rel:g" ${SYSROOT_DESTDIR}${libdir}/pkgconfig/*.pc
	fi
	if [ -d ${SYSROOT_DESTDIR}${datadir}/pkgconfig ]; then
		rel=${@os.path.relpath(d.getVar('base_prefix'), d.getVar('datadir') + "/pkgconfig")}
		sed -i -e "s:${base_prefix}:\${pcfiledir}/$rel:g" ${SYSROOT_DESTDIR}${datadir}/pkgconfig/*.pc
	fi
}
