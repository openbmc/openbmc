inherit chrpath

SYSROOT_PREPROCESS_FUNCS += "relocatable_binaries_preprocess relocatable_native_pcfiles"

python relocatable_binaries_preprocess() {
    rpath_replace(d.expand('${SYSROOT_DESTDIR}'), d)
}

relocatable_native_pcfiles() {
	for dir in ${libdir}/pkgconfig ${datadir}/pkgconfig; do
		files_template=${SYSROOT_DESTDIR}$dir/*.pc
		# Expand to any files matching $files_template
		files=$(echo $files_template)
		# $files_template and $files will differ if any files were found
		if [ "$files_template" != "$files" ]; then
			rel=$(realpath -m --relative-to=$dir ${base_prefix})
			sed -i -e "s:${base_prefix}:\${pcfiledir}/$rel:g" $files
		fi
	done
}
