# Ensure the uclibc-dev package is processed before uclibc-staticdev to allow
# *_nonshared.a libraries to be packaged in the uclibc-dev package.
PACKAGES = "ldd uclibc-utils uclibc-gconv uclibc-thread-db uclibc-argp uclibc-backtrace uclibc-libcrypt uclibc-libintl uclibc-libnsl uclibc-libresolv uclibc-libm uclibc-libdl uclibc-libutil uclibc-libpthread uclibc-librt ${PN}-dbg ${PN}-dev ${PN}-staticdev ${PN}-doc ${PN}"

FILES_uclibc-libcrypt = "${base_libdir}/libcrypt*.so.* ${base_libdir}/libcrypt-*.so"
FILES_uclibc-libintl = "${base_libdir}/libintl*.so.* ${base_libdir}/libintl-*.so"
FILES_uclibc-libnsl = "${base_libdir}/libnsl*.so.* ${base_libdir}/libnsl-*.so"
FILES_uclibc-libresolv = "${base_libdir}/libresolv*.so.* ${base_libdir}/libresolv-*.so"
FILES_uclibc-libm = "${base_libdir}/libm*.so.* ${base_libdir}/libm-*.so"
FILES_uclibc-libdl = "${base_libdir}/libdl*.so.* ${base_libdir}/libdl-*.so"
FILES_uclibc-libutil = "${base_libdir}/libutil*.so.* ${base_libdir}/libutil-*.so"
FILES_uclibc-libpthread = "${base_libdir}/libpthread*.so.* ${base_libdir}/libpthread-*.so"
FILES_uclibc-librt = "${base_libdir}/librt*.so.* ${base_libdir}/librt-*.so"

FILES_ldd = "${bindir}/ldd"
FILES_uclibc-utils = "${bindir} ${sbindir}"
FILES_uclibc-gconv = "${libdir}/gconv"
FILES_uclibc-thread-db = "${base_libdir}/libthread_db*"
FILES_uclibc-argp = "${base_libdir}/libuargp-*.so ${base_libdir}/libuargp*.so.*"
FILES_uclibc-backtrace = "${base_libdir}/libubacktrace-*.so ${base_libdir}/libubacktrace*.so.*"

# The last line (gdb and lib1) is for uclinux-uclibc builds
uclibc_baselibs = "${base_libdir}/ld*.so.* ${base_libdir}/ld*.so \
                   ${base_libdir}/libc*.so.* ${base_libdir}/libuClibc-*.so \
                   ${libdir}/libc.gdb ${libdir}/libc ${base_libdir}/lib1.so \
                  "
FILES_SOLIBSDEV = "${libdir}/lib*${SOLIBSDEV}"
FILES_${PN}-dev += "\
        ${libdir}/lib*.so \
        ${libdir}/*_nonshared.a \
        ${libdir}/[S]*crt[1in].o \
        ${libdir}/crtreloc*.o \
        ${includedir}/*.h ${includedir}/*/*.h \
       "
FILES_${PN} = "${sysconfdir} ${uclibc_baselibs} /sbin/ldconfig \
               ${libexecdir} ${datadir}/zoneinfo ${libdir}/locale"
