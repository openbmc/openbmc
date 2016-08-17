inherit chrpath

SYSROOT_PREPROCESS_FUNCS += "relocatable_binaries_preprocess"

python relocatable_binaries_preprocess() {
    rpath_replace(d.expand('${SYSROOT_DESTDIR}'), d)
}
