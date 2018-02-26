require gdb.inc
require gdb-${PV}.inc

inherit python3-dir

EXTRA_OEMAKE_append_libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                 gl_cv_func_gettimeofday_clobber=no \
                                "

do_configure_prepend() {
	if [ "${@bb.utils.filter('PACKAGECONFIG', 'python', d)}" ]; then
		cat > ${WORKDIR}/python << EOF
#!/bin/sh
case "\$2" in
	--includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}/" ;;
	--ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}${PYTHON_ABI}" ;;
	--exec-prefix) echo "${exec_prefix}" ;;
	*) exit 1 ;;
esac
exit 0
EOF
		chmod +x ${WORKDIR}/python
	fi
}
CFLAGS_append_libc-musl = " -Drpl_gettimeofday=gettimeofday"
