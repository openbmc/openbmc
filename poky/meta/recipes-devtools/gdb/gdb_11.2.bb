require gdb-common.inc

inherit gettext pkgconfig

#LDFLAGS:append = " -s"
#export CFLAGS:append=" -L${STAGING_LIBDIR}"

# cross-canadian must not see this
PACKAGES =+ "gdbserver"
FILES:gdbserver = "${bindir}/gdbserver"

require gdb.inc

inherit python3-dir

EXTRA_OEMAKE:append:libc-musl = "\
                                 gt_cv_func_gnugettext1_libc=yes \
                                 gt_cv_func_gnugettext2_libc=yes \
                                 gl_cv_func_working_strerror=yes \
                                 gl_cv_func_strerror_0_works=yes \
                                 gl_cv_func_gettimeofday_clobber=no \
                                "

do_configure:prepend() {
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

