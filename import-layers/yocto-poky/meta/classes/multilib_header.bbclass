inherit siteinfo

# If applicable on the architecture, this routine will rename the header and
# add a unique identifier to the name for the ABI/bitsize that is being used.
# A wrapper will be generated for the architecture that knows how to call
# all of the ABI variants for that given architecture.
#
oe_multilib_header() {

	case ${HOST_OS} in
	*-musl*)
		return
		;;
	*)
	esac
        # For MIPS: "n32" is a special case, which needs to be
        # distinct from both 64-bit and 32-bit.
        case ${TARGET_ARCH} in
        mips*)  case "${MIPSPKGSFX_ABI}" in
                "-n32")
                       ident=n32   
                       ;;
                *)     
                       ident=${SITEINFO_BITS}
                       ;;
                esac
                ;;
        *)      ident=${SITEINFO_BITS}
        esac
	for each_header in "$@" ; do
	   if [ ! -f "${D}/${includedir}/$each_header" ]; then
	      bberror "oe_multilib_header: Unable to find header $each_header."
	      continue
	   fi
	   stem=$(echo $each_header | sed 's#\.h$##')
	   # if mips64/n32 set ident to n32
	   mv ${D}/${includedir}/$each_header ${D}/${includedir}/${stem}-${ident}.h

	   sed -e "s#ENTER_HEADER_FILENAME_HERE#${stem}#g" ${COREBASE}/scripts/multilib_header_wrapper.h > ${D}/${includedir}/$each_header
	done
}

# Dependencies on arch variables like MIPSPKGSFX_ABI can be problematic.
# We don't need multilib headers for native builds so brute force things.
oe_multilib_header_class-native () {
	return
}

# Nor do we need multilib headers for nativesdk builds.
oe_multilib_header_class-nativesdk () {
	return
}
