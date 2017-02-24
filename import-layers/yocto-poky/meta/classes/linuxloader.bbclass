
linuxloader () {
	case ${TARGET_ARCH} in
		powerpc | microblaze )
			dynamic_loader="${base_libdir}/ld.so.1"
			;;
		mipsisa32r6el | mipsisa32r6 | mipsisa64r6el | mipsisa64r6)
			dynamic_loader="${base_libdir}/ld-linux-mipsn8.so.1"
			;;
		mips* )
			dynamic_loader="${base_libdir}/ld.so.1"
			;;
		powerpc64)
			dynamic_loader="${base_libdir}/ld64.so.1"
			;;
		x86_64)
			dynamic_loader="${base_libdir}/ld-linux-x86-64.so.2"
			;;
		i*86 )
			dynamic_loader="${base_libdir}/ld-linux.so.2"
			;;
		arm )
			dynamic_loader="${base_libdir}/ld-linux.so.3"
			;;
		* )
			dynamic_loader="/unknown_dynamic_linker"
			;;
	esac
	echo $dynamic_loader
}
