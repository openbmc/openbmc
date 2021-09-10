require gcc-configure-xilinx-standalone.inc

COMPATIBLE_HOST = "${HOST_SYS}"

python do_multilib_install_xilinx-standalone_class-target () {
    pass
}

standalone_fixup () {
	(
		cd ${D}${libdir}
		for each in ${TARGET_SYS}/*/* ; do
			ln -s $each $(basename $each)
		done
	)

	# Apparently we can end up with an empty /lib occasionally
        find ${D}/lib -type d | sort -r | xargs rmdir || :
}

FIXUP_FUNCTION = ""
FIXUP_FUNCTION_xilinx-standalone_class-target = " standalone_fixup"

do_install[postfuncs] .= "${FIXUP_FUNCTION}"

FILES_${PN}-dev_append_xilinx-standalone_class-target = " \
	${libdir}/*.a \
	${libdir}/*.o \
"
